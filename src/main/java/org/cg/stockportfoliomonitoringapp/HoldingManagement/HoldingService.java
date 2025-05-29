// src/main/java/org/cg/stockportfoliomonitoringapp/holding/service/HoldingService.java
package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.RequiredArgsConstructor;

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
    private final RestTemplate restTemplate;

    @Value("${xano.mockdata.stocks.url}")
    private String xanoMockDataStocksUrl;

    private Map<String, StockAPIDto> getExternalStockDataMap() {
        try {
            ResponseEntity<List<StockAPIDto>> responseEntity = restTemplate.exchange(
                xanoMockDataStocksUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<StockAPIDto>>() {}
            );
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody().stream()
                       .collect(Collectors.toMap(StockAPIDto::getSymbol, stock -> stock));
            } else {
                throw new RuntimeException("Failed to get stock data from external API. Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to external stock API: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new stock holding for a user.
     * stockName is now provided by the client.
     */
    public HoldingGainDetailsResponse addHolding(Long userId, String stockSymbol, String stockName, Integer quantity, Double buyPrice) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty() || stockName == null || stockName.trim().isEmpty() || quantity == null || quantity <= 0 || buyPrice == null || buyPrice <= 0) {
            throw new IllegalArgumentException("Invalid input for adding holding. Please check stock symbol, name, quantity, and buy price.");
        }

        Optional<Holding> existingHolding = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol);
        if (existingHolding.isPresent()) {
            throw new IllegalArgumentException("Holding for stock '" + stockSymbol + "' already exists for user ID " + userId + ". Please use the 'PUT' request to update it.");
        }

        Map<String, StockAPIDto> allStockData = getExternalStockDataMap();
        StockAPIDto currentStockInfo = allStockData.get(stockSymbol.trim().toUpperCase());
        if (currentStockInfo == null) {
            throw new NoSuchElementException("Stock symbol '" + stockSymbol + "' not found in external market data. Please check the symbol.");
        }

        Holding newHolding = new Holding();
        newHolding.setUserId(userId);
        newHolding.setStockSymbol(currentStockInfo.getSymbol()); // Use symbol from API for consistency
        newHolding.setQuantity(quantity);
        newHolding.setBuyPrice(buyPrice);

        // Set stockName from the request body (client provides this)
        newHolding.setStockName(stockName);
        // Set sector from the external API data (client does NOT provide this)
        newHolding.setSector(currentStockInfo.getSector());

        // IMPORTANT: stockId is not being populated here.
        // If your DB column 'stock_id' is NOT NULL, this will cause an error.
        // You MUST make it nullable in your database (ALTER TABLE holdings MODIFY COLUMN stock_id BIGINT NULL;).
        // If your Xano API provides a stock ID, you would add it to StockAPIDto and set it here.
        // newHolding.setStockId(...);

        Holding savedHolding = holdingRepository.save(newHolding);
        return buildHoldingGainDetailsResponse(savedHolding, currentStockInfo, "Holding successfully added.");
    }

    /**
     * Updates an existing stock holding for a user.
     * stockName is now provided by the client.
     */
    public HoldingGainDetailsResponse updateHolding(Long userId, String stockSymbol, String stockName, Integer newQuantity, Double newBuyPrice) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty() || stockName == null || stockName.trim().isEmpty() || newQuantity == null || newQuantity <= 0 || newBuyPrice == null || newBuyPrice <= 0) {
            throw new IllegalArgumentException("Invalid input for updating holding: Stock symbol, name, new quantity, and new buy price are required and must be positive.");
        }

        Holding existingHolding = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol)
                                                .orElseThrow(() -> new NoSuchElementException("Holding for stock '" + stockSymbol + "' not found for user ID " + userId + ". Cannot update."));

        Map<String, StockAPIDto> allStockData = getExternalStockDataMap();
        StockAPIDto currentStockInfo = allStockData.get(stockSymbol.trim().toUpperCase());
        if (currentStockInfo == null) {
            throw new NoSuchElementException("Stock symbol '" + stockSymbol + "' not found in external market data. Cannot update without valid market data.");
        }

        existingHolding.setQuantity(newQuantity);
        existingHolding.setBuyPrice(newBuyPrice);

        // Update stockName from the request body (client provides this)
        existingHolding.setStockName(stockName);
        // Update sector from the external API data (client does NOT provide this)
        existingHolding.setSector(currentStockInfo.getSector());

        Holding savedHolding = holdingRepository.save(existingHolding);
        return buildHoldingGainDetailsResponse(savedHolding, currentStockInfo, "Holding successfully updated.");
    }

    /**
     * Deletes a holding for a user.
     */
    public String deleteHolding(Long userId, String stockSymbol) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty for delete operation.");
        }

        Holding holdingToDelete = holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, stockSymbol)
                                                .orElseThrow(() -> new NoSuchElementException("Holding for stock '" + stockSymbol + "' not found for user ID " + userId + ". Cannot delete."));

        holdingRepository.delete(holdingToDelete);
        return "Holding for stock '" + stockSymbol + "' deleted successfully for user ID " + userId + ".";
    }

    /**
     * Retrieves all holdings for a specific user, with live market data.
     */
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
                // For display, using live data for company name and sector is often preferred
                // as it's the most up-to-date.
                dto.setCompanyName(liveStockDetails.getName());
                dto.setSector(liveStockDetails.getSector());
            } else {
                // Fallback to stored names if live data is unavailable, but warn.
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

    /**
     * Fetches all stock details from the external mock API.
     */
    public List<StockAPIDto> getAllStockDetailsFromExternalAPI() {
        return new ArrayList<>(getExternalStockDataMap().values());
    }

    /**
     * Private helper to build a consistent HoldingGainDetailsResponse.
     */
    private HoldingGainDetailsResponse buildHoldingGainDetailsResponse(Holding holding, StockAPIDto stockApiInfo, String message) {
        HoldingGainDetailsResponse response = new HoldingGainDetailsResponse();
        response.setHoldingId(holding.getId()); // Use 'id' from Holding entity
        response.setStockName(holding.getStockName()); // Use the stored stockName (from request)
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
        response.setSector(holding.getSector()); // Use the stored sector
        response.setMessage(message);
        return response;
    }
}