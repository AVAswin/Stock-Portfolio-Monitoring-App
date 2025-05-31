package org.cg.stockportfoliomonitoringapp.AlertsManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepo;

    @InjectMocks
    private AlertService alertService;

    private MockedStatic<StockService> mockedStaticStockService;

    @BeforeEach
    void setUp() {

        mockedStaticStockService = mockStatic(StockService.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockedStaticStockService != null) {
            mockedStaticStockService.close();
        }
    }


    @Test
    void checkAndTriggerAlertsAlertsSuccessfully() {
        Alert alert1 = new Alert();
        alert1.setId(1L);
        alert1.setUserId(101L);
        alert1.setSymbol("AAPL");
        alert1.setTargetPrice("150.00");

        Alert alert2 = new Alert();
        alert2.setId(2L);
        alert2.setUserId(102L);
        alert2.setSymbol("GOOG");
        alert2.setTargetPrice("100.00");

        List<Alert> alerts = Arrays.asList(alert1, alert2);
        when(alertRepo.findAll()).thenReturn(alerts);

        StocksDTO stockAAPL = new StocksDTO();
        stockAAPL.setCurrentPrice(155.00);
        stockAAPL.setSymbol("AAPL");

        StocksDTO stockGOOG = new StocksDTO();
        stockGOOG.setCurrentPrice(95.00);
        stockGOOG.setSymbol("GOOG");

        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("AAPL", stockAAPL);
        stocksMap.put("GOOG", stockGOOG);

        mockedStaticStockService.when(StockService::getAllStocks).thenReturn(stocksMap);
        alertService.checkAndTriggerAlerts();
        verify(alertRepo, times(1)).findAll();
        mockedStaticStockService.verify(StockService::getAllStocks, times(1));
        verify(alertRepo, times(1)).save(argThat(alert ->
                alert.getSymbol().equals("AAPL") && alert.getGainOrLoss().equals("Gain of 3.33%")
        ));
        verify(alertRepo, times(1)).save(argThat(alert ->
                alert.getSymbol().equals("GOOG") && alert.getGainOrLoss().equals("Loss of 5.00%")
        ));
    }

    @Test
    void checkAndTriggerAlertssSymbolNotFound() {
        // Arrange
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setUserId(101L);
        alert.setSymbol("NONEXISTENT");
        alert.setTargetPrice("100.00");
        List<Alert> alerts = Collections.singletonList(alert);
        when(alertRepo.findAll()).thenReturn(alerts);
        mockedStaticStockService.when(StockService::getAllStocks).thenReturn(new HashMap<>());
        alertService.checkAndTriggerAlerts();
        verify(alertRepo, times(1)).findAll();
        mockedStaticStockService.verify(StockService::getAllStocks, times(1));
        verify(alertRepo, never()).save(any(Alert.class));
    }

    @Test
    void addAlert() {
        Alert newAlert = new Alert();
        newAlert.setUserId(103L);
        newAlert.setSymbol("MSFT");
        newAlert.setTargetPrice("200.00");

        StocksDTO stockMSFT = new StocksDTO();
        stockMSFT.setCurrentPrice(210.00);
        stockMSFT.setSymbol("MSFT");

        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("MSFT", stockMSFT);

        mockedStaticStockService.when(StockService::getAllStocks).thenReturn(stocksMap);
        when(alertRepo.save(any(Alert.class))).thenReturn(newAlert);
        AlertDTO result = alertService.addAlert(newAlert);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getHttpStatus());
        assertEquals(HttpStatus.OK.value(), result.getStatus());
        assertTrue(result.getMessage().contains("Alert Triggered -> Gain of 5.00%"));
        assertNotNull(result.getLocalDateTime());
        mockedStaticStockService.verify(StockService::getAllStocks, times(1));
        verify(alertRepo, times(1)).save(any(Alert.class));
    }

    @Test
    void addAlertConditionNotMet() {
        Alert newAlert = new Alert();
        newAlert.setUserId(104L);
        newAlert.setSymbol("AMZN");
        newAlert.setTargetPrice("150.00");
        StocksDTO stockAMZN = new StocksDTO();
        stockAMZN.setCurrentPrice(140.00);
        stockAMZN.setSymbol("AMZN");
        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("AMZN", stockAMZN);
        mockedStaticStockService.when(StockService::getAllStocks).thenReturn(stocksMap);
        when(alertRepo.save(any(Alert.class))).thenReturn(newAlert);
        AlertDTO result = alertService.addAlert(newAlert);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getHttpStatus());
        assertEquals(HttpStatus.OK.value(), result.getStatus());
        assertTrue(result.getMessage().contains("Alert Triggered -> Loss of 6.67%"));
        assertNotNull(result.getLocalDateTime());
        mockedStaticStockService.verify(StockService::getAllStocks, times(1));
        verify(alertRepo, times(1)).save(any(Alert.class));
    }

    @Test
    void addAlertThrowsExceptionSymbolNotFound() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setUserId(105L);
        newAlert.setSymbol("UNKNOWN");
        newAlert.setTargetPrice("100.00");

        mockedStaticStockService.when(StockService::getAllStocks).thenReturn(new HashMap<>());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            alertService.addAlert(newAlert);
        });

        assertEquals("Stock symbol not found in the available stock data.", thrown.getMessage());
        mockedStaticStockService.verify(StockService::getAllStocks, times(1));
        verify(alertRepo, never()).save(any(Alert.class));
    }
}