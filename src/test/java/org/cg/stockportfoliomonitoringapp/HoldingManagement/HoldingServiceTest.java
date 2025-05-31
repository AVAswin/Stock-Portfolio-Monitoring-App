package org.cg.stockportfoliomonitoringapp.HoldingManagement;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.InvalidRequestException;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HoldingServiceTest {

    @InjectMocks
    private HoldingService holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        holdingService = new HoldingService(holdingRepository);

        ReflectionTestUtils.setField(holdingService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(holdingService, "xanoMockDataStocksUrl", "http://mock-api/stocks");
    }

    @Test
    void testAddHoldingSuccess() {
        Long userId = 1L;
        String symbol = "AAPL";
        String name = "Apple Inc.";
        int qty = 5;
        double buyPrice = 100.0;

        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(userId, symbol))
                .thenReturn(Optional.empty());

        StockAPIDto stockDto = new StockAPIDto();
        stockDto.setSymbol("AAPL");
        stockDto.setCurrentPrice(150.0);
        stockDto.setSector("Tech");

        List<StockAPIDto> mockList = List.of(stockDto);

        when(restTemplate.exchange(
                eq("http://mock-api/stocks"),
                eq(org.springframework.http.HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<StockAPIDto>>>any()
        )).thenReturn(new org.springframework.http.ResponseEntity<>(mockList, org.springframework.http.HttpStatus.OK));

        Holding savedHolding = new Holding();
        savedHolding.setId(1L);
        savedHolding.setUserId(userId);
        savedHolding.setStockSymbol("AAPL");
        savedHolding.setStockName(name);
        savedHolding.setQuantity(qty);
        savedHolding.setBuyPrice(buyPrice);
        savedHolding.setSector("Tech");

        when(holdingRepository.save(any(Holding.class))).thenReturn(savedHolding);

        HoldingGainDetailsResponse response = holdingService.addHolding(userId, symbol, name, qty, buyPrice);

        assertEquals("AAPL", response.getStockSymbol());
        assertEquals(150.0, response.getCurrentPrice());
        assertEquals("Tech", response.getSector());
        assertTrue(response.getProfitOrLoss() > 0);
    }

    @Test
    void testAddHoldingInvalidInput_ThrowsException() {
        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                holdingService.addHolding(1L, "", "", -1, -10.0));
        assertTrue(ex.getMessage().contains("Invalid input"));
    }

    @Test
    void testAddHoldingDuplicate_ThrowsException() {
        when(holdingRepository.findByUserIdAndStockSymbolIgnoreCase(1L, "AAPL"))
                .thenReturn(Optional.of(new Holding()));

        assertThrows(InvalidRequestException.class, () ->
                holdingService.addHolding(1L, "AAPL", "Apple", 1, 100.0));
    }

    @Test
    void testGetAllStockDetailsFromExternalAPISuccess() {
        StockAPIDto stockDto = new StockAPIDto();
        stockDto.setSymbol("MSFT");
        stockDto.setCurrentPrice(200.0);
        stockDto.setSector("Tech");

        List<StockAPIDto> mockList = List.of(stockDto);

        when(restTemplate.exchange(
                anyString(),
                eq(org.springframework.http.HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<StockAPIDto>>>any()
        )).thenReturn(new org.springframework.http.ResponseEntity<>(mockList, org.springframework.http.HttpStatus.OK));

        List<StockAPIDto> stocks = holdingService.getAllStockDetailsFromExternalAPI();
        assertEquals(1, stocks.size());
        assertEquals("MSFT", stocks.get(0).getSymbol());
    }

    @Test
    void testGetAllStockDetailsFromExternalAPIFailure() {
        when(restTemplate.exchange(
                anyString(),
                eq(org.springframework.http.HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<StockAPIDto>>>any()
        )).thenThrow(new RuntimeException("API Down"));

        assertThrows(ResourceNotFoundException.class, () -> holdingService.getAllStockDetailsFromExternalAPI());
    }
}