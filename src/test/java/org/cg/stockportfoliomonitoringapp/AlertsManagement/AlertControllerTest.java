package org.cg.stockportfoliomonitoringapp.AlertsManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {


    @Mock
    private AlertService alertService;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertController alertController;

    private Alert testAlert1;
    private Alert testAlert2;
    private AlertDTO testAlertDTO;

    @BeforeEach
    void setUp() {

        testAlert1 = new Alert();
        testAlert1.setId(1L);
        testAlert1.setSymbol("AAPL");
        testAlert1.setTargetPrice("150.0");
        testAlert1.setUserId(101L);
        testAlert1.setGainOrLoss("Gain of 5.00%");

        testAlert2 = new Alert();
        testAlert2.setId(2L);
        testAlert2.setSymbol("GOOG");
        testAlert2.setTargetPrice("2000.0");
        testAlert2.setUserId(102L);
        testAlert2.setGainOrLoss("Loss of 2.00%");


        testAlertDTO = new AlertDTO();
        testAlertDTO.setMessage("Alert Triggered -> Gain of 5.00%");
        testAlertDTO.setLocalDateTime(LocalDateTime.now());
        testAlertDTO.setHttpStatus(HttpStatus.OK);
        testAlertDTO.setStatus(HttpStatus.OK.value());
    }

    @Test
    void testAllAlerts() {

        List<Alert> allAlerts = Arrays.asList(testAlert1, testAlert2);
        when(alertRepository.findAll()).thenReturn(allAlerts);
        List<Alert> result = alertController.getAlert();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertEquals("GOOG", result.get(1).getSymbol());
        verify(alertRepository, times(1)).findAll();
        verifyNoInteractions(alertService);
    }

    @Test
    void testGetByUserId() {

        Long userIdToTest = 101L;
        List<Alert> userSpecificAlerts = Arrays.asList(testAlert1);
        when(alertRepository.findByUserId(userIdToTest)).thenReturn(userSpecificAlerts);
        List<Alert> result = alertController.getAlertsByUserId(userIdToTest);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertEquals(userIdToTest, result.get(0).getUserId());
        verify(alertRepository, times(1)).findByUserId(userIdToTest);
        verifyNoMoreInteractions(alertService);
    }

    @Test
    void testAddAlerts() {

        Alert alertToAdd = testAlert1;
        when(alertService.addAlert(any(Alert.class))).thenReturn(testAlertDTO);
        ResponseEntity<AlertDTO> responseEntity = alertController.addAlert(alertToAdd);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(testAlertDTO.getMessage(), responseEntity.getBody().getMessage());
        assertEquals(testAlertDTO.getStatus(), responseEntity.getBody().getStatus());
        verify(alertService, times(1)).addAlert(alertToAdd);
        verifyNoInteractions(alertRepository);
    }

    @Test
    void testAddAlertException() {
        Alert alertToAdd = testAlert1;
        String errorMessage = "Stock symbol not found in the available stock data.";
        when(alertService.addAlert(any(Alert.class)))
                .thenThrow(new RuntimeException(errorMessage));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            alertController.addAlert(alertToAdd);
        });
        assertEquals(errorMessage, thrown.getMessage());
        verify(alertService, times(1)).addAlert(alertToAdd);
        verifyNoInteractions(alertRepository);
    }
}