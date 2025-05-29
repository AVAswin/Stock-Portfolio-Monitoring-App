package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.RequiredArgsConstructor;

import org.cg.stockportfoliomonitoringapp.ExceptionManagement.InvalidRequestException;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoldingService {


    private final HoldingRepository holdingRepository;
    private final RestTemplate restTemplate=new RestTemplate();


    @Value("${xano.mockdata.stocks.url}")
    private String xanoMockDataStocksUrl;

    public  Map<String, StockAPIDto> getExternalStockDataMap() {
        try {
            ResponseEntity<List<StockAPIDto>> responseEntity = restTemplate.exchange(
                xanoMockDataStocksUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<StockAPIDto>>() {}
            );
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody().stream()
                       .collect(Collectors.toMap(StockAPIDto::getSymbol, stock -> stock));
            } else {
                throw new ResourceNotFoundException("Failed to get stock data from external API. Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Could not connect to external stock API: " + e.getMessage());
        }
    }

    public HoldingGainDetailsResponse addHolding(Long userId, String stockSymbol, String stockName, Integer quantity, Double buyPrice) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty() || stockName == null || stockName.trim().isEmpty() || quantity == null || quantity <= 0 || buyPrice == null || buyPrice <= 0) {
            throw new InvalidRequestException("Invalid input for adding holding. Please check stock symbol, name, quantity, and buy price.");
        }

        Optional<Holding> existingHolding = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol);
        if (existingHolding.isPresent()) {
            throw new InvalidRequestException("Holding for stock '" + stockSymbol + "' already exists for user ID " + userId + ". Please use the 'PUT' request to update it.");
        }

        Map<String, StockAPIDto> allStockData = getExternalStockDataMap();
        StockAPIDto currentStockInfo = allStockData.get(stockSymbol.trim().toUpperCase());
        if (currentStockInfo == null) {
            throw new InvalidRequestException("Stock symbol '" + stockSymbol + "' not found in external market data. Please check the symbol.");
        }

        Holding newHolding = new Holding();
        newHolding.setUserId(userId);
        newHolding.setStockSymbol(currentStockInfo.getSymbol()); 
        newHolding.setQuantity(quantity);
        newHolding.setBuyPrice(buyPrice);
        newHolding.setStockName(stockName);
        newHolding.setSector(currentStockInfo.getSector());
        
        Holding savedHolding = holdingRepository.save(newHolding);
        return buildHoldingGainDetailsResponse(savedHolding, currentStockInfo, "Holding successfully added.");
    }

    public HoldingGainDetailsResponse updateHolding(Long userId, String stockSymbol, String stockName, Integer newQuantity, Double newBuyPrice) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty() || stockName == null || stockName.trim().isEmpty() || newQuantity == null || newQuantity <= 0 || newBuyPrice == null || newBuyPrice <= 0) {
            throw new InvalidRequestException("Invalid input for updating holding: Stock symbol, name, new quantity, and new buy price are required and must be positive.");
        }

        Holding existingHolding = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol)
                                                .orElseThrow(() -> new NoSuchElementException("Holding for stock '" + stockSymbol + "' not found for user ID " + userId + ". Cannot update."));

        Map<String, StockAPIDto> allStockData = getExternalStockDataMap();
        StockAPIDto currentStockInfo = allStockData.get(stockSymbol.trim().toUpperCase());
        if (currentStockInfo == null) {
            throw new InvalidRequestException("Stock symbol '" + stockSymbol + "' not found in external market data. Cannot update without valid market data.");
        }

        existingHolding.setQuantity(newQuantity);
        existingHolding.setBuyPrice(newBuyPrice);
        existingHolding.setStockName(stockName);
        existingHolding.setSector(currentStockInfo.getSector());

        Holding savedHolding = holdingRepository.save(existingHolding);
        return buildHoldingGainDetailsResponse(savedHolding, currentStockInfo, "Holding successfully updated.");
    }

    public String deleteHolding(Long userId, String stockSymbol) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new InvalidRequestException("Stock symbol cannot be empty for delete operation.");
        }

        Holding holdingToDelete = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol)
                                                .orElseThrow(() -> new NoSuchElementException("Holding for stock '" + stockSymbol + "' not found for user ID " + userId + ". Cannot delete."));

        holdingRepository.delete(holdingToDelete);
        return "Holding for stock '" + stockSymbol + "' deleted successfully for user ID " + userId + ".";
    }
    
    public HoldingResponseDTO getHoldingsForUser(Long userId) {
        List<Holding> userHoldings = holdingRepository.findByUserId(userId);

        Map<String, StockAPIDto> stockDetailsMap = getExternalStockDataMap();

        List<HoldingStatusDTO> statusList = new ArrayList<>();
        double totalPortfolioValue = 0.0;

        for (Holding userHolding : userHoldings) {
            HoldingStatusDTO dto = new HoldingStatusDTO();
            dto.setSymbol(userHolding.getStockSymbol());
            dto.setQuantity(userHolding.getQuantity());
            dto.setBuyPrice(userHolding.getBuyPrice());

            StockAPIDto liveStockDetails = stockDetailsMap.get(userHolding.getStockSymbol().toUpperCase());
            double currentPrice = 0.0;
            if (liveStockDetails != null) {
                currentPrice = liveStockDetails.getCurrentPrice();
           
                dto.setCompanyName(liveStockDetails.getName());
                dto.setSector(liveStockDetails.getSector());
            } else {
         
                dto.setCompanyName(userHolding.getStockName() != null ? userHolding.getStockName() : "N/A - Data Unavailable");
                dto.setSector(userHolding.getSector() != null ? userHolding.getSector() : "N/A - Data Unavailable");
                System.err.println("Warning: Live data for stock '" + userHolding.getStockSymbol() + "' not found from external API. Using 0 for current price for calculations.");
            }
            dto.setCurrentPrice(currentPrice);

            double profitOrLoss = (currentPrice - userHolding.getBuyPrice()) * userHolding.getQuantity();
            double gainPercentage = 0.0;
            if (userHolding.getBuyPrice() != 0) {
                gainPercentage = (profitOrLoss / (userHolding.getBuyPrice() * userHolding.getQuantity())) * 100;
            }

            dto.setProfitOrLoss(profitOrLoss);
            dto.setGainPercentage(gainPercentage);

            totalPortfolioValue += currentPrice * userHolding.getQuantity();
            statusList.add(dto);
        }
        return new HoldingResponseDTO(statusList, totalPortfolioValue, "Holdings fetched successfully with live data.");
    }


    public List<StockAPIDto> getAllStockDetailsFromExternalAPI() {
        return new ArrayList<>(getExternalStockDataMap().values());
    }

    private HoldingGainDetailsResponse buildHoldingGainDetailsResponse(Holding holding, StockAPIDto stockApiInfo, String message) {
        HoldingGainDetailsResponse response = new HoldingGainDetailsResponse();
        response.setHoldingId(holding.getId()); 
        response.setStockName(holding.getStockName()); 
        response.setStockSymbol(holding.getStockSymbol());
        response.setQuantity(holding.getQuantity());
        response.setBuyPrice(holding.getBuyPrice());
        response.setCurrentPrice(stockApiInfo.getCurrentPrice());

        double totalBuyValue = holding.getQuantity() * holding.getBuyPrice();
        double currentMarketValue = holding.getQuantity() * stockApiInfo.getCurrentPrice();
        double profitOrLoss = currentMarketValue - totalBuyValue;
        double gainPercent = 0.0;
        if (totalBuyValue != 0) {
            gainPercent = (profitOrLoss / totalBuyValue) * 100;
        }

        response.setTotalBuyValue(totalBuyValue);
        response.setCurrentMarketValue(currentMarketValue);
        response.setProfitOrLoss(profitOrLoss);
        response.setGainPercent(gainPercent);
        response.setSector(holding.getSector()); 
        response.setMessage(message);
        return response;
    } 
    public List<Holding> getHoldingsByUserId(Long userId) {
        return holdingRepository.findByUserId(userId);
    }
}