package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

class HoldingServiceTest {

    @InjectMocks
    private HoldingService holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inject a dummy URL since we're not making real HTTP calls
        ReflectionTestUtils.setField(holdingService, "xanoMockDataStocksUrl", "http://mock-api/stocks");
    }

    @Test
    void testAddHolding_Success() {
        Long userId = 1L;
        String symbol = "AAPL";
        String stockName = "Apple Inc.";
        int quantity = 10;
        double buyPrice = 150.0;

        StockAPIDto stockData = new StockAPIDto();
        stockData.setSymbol("AAPL");
        stockData.setCurrentPrice(160.0);
        stockData.setSector("Technology");

        // Mocking external API
        List<StockAPIDto> apiList = List.of(stockData);
        ResponseEntity<List<StockAPIDto>> responseEntity = new ResponseEntity<>(apiList, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class))
        ).thenReturn(responseEntity);

        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, symbol)).thenReturn(Optional.empty());

        Holding savedHolding = new Holding();
        savedHolding.setId(100L);
        savedHolding.setUserId(userId);
        savedHolding.setStockSymbol(symbol);
        savedHolding.setStockName(stockName);
        savedHolding.setQuantity(quantity);
        savedHolding.setBuyPrice(buyPrice);
        savedHolding.setSector("Technology");

        when(holdingRepository.save(any(Holding.class))).thenReturn(savedHolding);

        HoldingGainDetailsResponse response = holdingService.addHolding(userId, symbol, stockName, quantity, buyPrice);

        assertEquals("Holding successfully added.", response.getMessage());
        assertEquals(100L, response.getHoldingId());
        assertEquals(1600.0, response.getCurrentMarketValue());
        assertEquals(1500.0, response.getTotalBuyValue());
    }

    @Test
    void testAddHolding_AlreadyExists() {
        Long userId = 1L;
        String symbol = "AAPL";

        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, symbol))
                .thenReturn(Optional.of(new Holding()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            holdingService.addHolding(userId, symbol, "Apple", 10, 150.0);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testDeleteHolding_Success() {
        Long userId = 1L;
        String symbol = "AAPL";

        Holding holding = new Holding();
        holding.setUserId(userId);
        holding.setStockSymbol(symbol);

        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, symbol))
                .thenReturn(Optional.of(holding));

        String result = holdingService.deleteHolding(userId, symbol);
        assertTrue(result.contains("deleted successfully"));

        verify(holdingRepository, times(1)).delete(holding);
    }

    @Test
    void testDeleteHolding_NotFound() {
        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(1L, "MSFT"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            holdingService.deleteHolding(1L, "MSFT");
        });

        assertTrue(exception.getMessage().contains("not found"));
    }
}
