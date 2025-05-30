package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @MockBean
    private AlertRepository alertRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllAlerts_Success() throws Exception {
        Alert alert1 = createSampleAlert(1L, "AAPL", "150", "gain");
        Alert alert2 = createSampleAlert(2L, "GOOGL", "100", "loss");

        Mockito.when(alertRepository.findAll()).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[1].symbol").value("GOOGL"));
    }

    @Test
    void testAddAlert_Success() throws Exception {
        Alert inputAlert = createSampleAlert(null, "MSFT", "300", "gain");

        AlertDTO alertDTO = new AlertDTO();
        alertDTO.setMessage("Alert created successfully");
        alertDTO.setStatus(201);
        alertDTO.setHttpStatus(HttpStatus.CREATED);
        alertDTO.setLocalDateTime(LocalDateTime.now());

        Mockito.when(alertService.addAlert(any(Alert.class))).thenReturn(alertDTO);

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAlert)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Alert created successfully"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.httpStatus").value("CREATED"));
    }

    @Test
    void testGetAlertsByUserId_Success() throws Exception {
        Long userId = 1L;
        Alert alert = createSampleAlert(1L, "TSLA", "500", "gain");

        Mockito.when(alertRepository.findByUserId(eq(userId))).thenReturn(List.of(alert));

        mockMvc.perform(get("/alerts/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].symbol").value("TSLA"));
    }

    private Alert createSampleAlert(Long id, String symbol, String targetPrice, String gainOrLoss) {
        Alert alert = new Alert();
        alert.setId(id);
        alert.setUserId(1L);
        alert.setSymbol(symbol);
        alert.setTargetPrice(targetPrice);
        alert.setGainOrLoss(gainOrLoss);
        return alert;
    }
}
