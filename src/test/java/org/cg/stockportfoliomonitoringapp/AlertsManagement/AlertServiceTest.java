package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    @Mock
    private AlertRepository alertRepo;

    @Mock
    private StockService stockService; // Mock the instance of StockService

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkAndTriggerAlerts_triggersAlertsSuccessfully() {
        // Arrange
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
        when(alertRepo.findAll()).thenReturn(alerts); // Mock the repository call

        StocksDTO stockAAPL = new StocksDTO();
        stockAAPL.setCurrentPrice(155.00); // Set current price for Apple
        stockAAPL.setSymbol("AAPL"); // Set symbol for Apple

        StocksDTO stockGOOG = new StocksDTO();
        stockGOOG.setCurrentPrice(95.00); // Set current price for Google
        stockGOOG.setSymbol("GOOG"); // Set symbol for Google

        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("AAPL", stockAAPL);
        stocksMap.put("GOOG", stockGOOG);
        when(stockService.getAllStocks()).thenReturn(stocksMap); // Mock the stock service call

        // Act
        alertService.checkAndTriggerAlerts(); // Call the method to be tested

        // Assert
        verify(alertRepo, times(1)).findAll(); // Verify findAll was called once
        verify(stockService, times(1)).getAllStocks(); // Verify getAllStocks was called once

        // Verify save calls for each alert with updated gainOrLoss message
        verify(alertRepo, times(1)).save(argThat(alert ->
                alert.getSymbol().equals("AAPL") && alert.getGainOrLoss().equals("Gain of 3.33%") // Check for Apple alert
        ));
        verify(alertRepo, times(1)).save(argThat(alert ->
                alert.getSymbol().equals("GOOG") && alert.getGainOrLoss().equals("Loss of 5.00%") // Check for Google alert
        ));
    }

    @Test
    void checkAndTriggerAlerts_handlesSymbolNotFound() {
        // Arrange
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setUserId(101L);
        alert.setSymbol("NONEXISTENT"); // Set a non-existent symbol
        alert.setTargetPrice("100.00"); // Set a target price

        List<Alert> alerts = Collections.singletonList(alert);
        when(alertRepo.findAll()).thenReturn(alerts); // Mock findAll to return the alert
        when(stockService.getAllStocks()).thenReturn(new HashMap<>()); // Mock getAllStocks to return an empty map

        // Act
        alertService.checkAndTriggerAlerts(); // Call the method to be tested

        // Assert
        verify(alertRepo, times(1)).findAll(); // Verify findAll was called once
        verify(stockService, times(1)).getAllStocks(); // Verify getAllStocks was called once
        verify(alertRepo, never()).save(any(Alert.class)); // Verify save was never called
    }

    @Test
    void addAlert_addsAlertSuccessfully_conditionMet() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setUserId(103L);
        newAlert.setSymbol("MSFT"); // Set symbol for Microsoft
        newAlert.setTargetPrice("200.00"); // Set target price

        StocksDTO stockMSFT = new StocksDTO();
        stockMSFT.setCurrentPrice(210.00); // Current price higher than target
        stockMSFT.setSymbol("MSFT"); // Set symbol

        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("MSFT", stockMSFT);
        when(stockService.getAllStocks()).thenReturn(stocksMap); // Mock getAllStocks
        when(alertRepo.save(any(Alert.class))).thenReturn(newAlert); // Mock the save operation

        // Act
        AlertDTO result = alertService.addAlert(newAlert); // Call the method to be tested

        // Assert
        assertNotNull(result); // Result should not be null
        assertEquals(HttpStatus.OK, result.getHttpStatus()); // HTTP status should be OK
        assertEquals(HttpStatus.OK.value(), result.getStatus()); // Status code should be 200
        // Your AlertService overwrites the message to always be "Alert Triggered" even if initially "Alert Saved"
        assertTrue(result.getMessage().contains("Alert Triggered -> Gain of 5.00%")); // Message content check
        assertNotNull(result.getLocalDateTime()); // Local date time should not be null
        verify(stockService, times(1)).getAllStocks(); // Verify getAllStocks was called once
        verify(alertRepo, times(1)).save(any(Alert.class)); // Verify save was called once
    }

    @Test
    void addAlert_addsAlertSuccessfully_conditionNotMet() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setUserId(104L);
        newAlert.setSymbol("AMZN"); // Set symbol for Amazon
        newAlert.setTargetPrice("150.00"); // Set target price

        StocksDTO stockAMZN = new StocksDTO();
        stockAMZN.setCurrentPrice(140.00); // Current price lower than target
        stockAMZN.setSymbol("AMZN"); // Set symbol

        Map<String, StocksDTO> stocksMap = new HashMap<>();
        stocksMap.put("AMZN", stockAMZN);
        when(stockService.getAllStocks()).thenReturn(stocksMap); // Mock getAllStocks
        when(alertRepo.save(any(Alert.class))).thenReturn(newAlert); // Mock the save operation

        // Act
        AlertDTO result = alertService.addAlert(newAlert); // Call the method to be tested

        // Assert
        assertNotNull(result); // Result should not be null
        assertEquals(HttpStatus.OK, result.getHttpStatus()); // HTTP status should be OK
        assertEquals(HttpStatus.OK.value(), result.getStatus()); // Status code should be 200
        // Your AlertService overwrites the message to always be "Alert Triggered" even if initially "Alert Saved"
        assertTrue(result.getMessage().contains("Alert Triggered -> Loss of 6.67%")); // Message content check
        assertNotNull(result.getLocalDateTime()); // Local date time should not be null
        verify(stockService, times(1)).getAllStocks(); // Verify getAllStocks was called once
        verify(alertRepo, times(1)).save(any(Alert.class)); // Verify save was called once
    }

    @Test
    void addAlert_throwsException_symbolNotFound() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setUserId(105L);
        newAlert.setSymbol("UNKNOWN"); // Set an unknown symbol
        newAlert.setTargetPrice("100.00"); // Set a target price

        when(stockService.getAllStocks()).thenReturn(new HashMap<>()); // Mock getAllStocks to return an empty map

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            alertService.addAlert(newAlert); // Call the method that should throw an exception
        });

        assertEquals("Stock symbol not found in the available stock data.", thrown.getMessage()); // Verify exception message
        verify(stockService, times(1)).getAllStocks(); // Verify getAllStocks was called once
        verify(alertRepo, never()).save(any(Alert.class)); // Verify save was never called
    }
}