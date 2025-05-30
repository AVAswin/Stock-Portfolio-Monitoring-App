package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cg.stockportfoliomonitoringapp.HoldingManagement.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(HoldingController.class)
@AutoConfigureMockMvc(addFilters = false)
class HoldingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HoldingService holdingService;

    @Autowired
    private ObjectMapper objectMapper;

    private HoldingUpdateRequest request;
    private HoldingGainDetailsResponse response;

    @BeforeEach
    void setup() {
        request = new HoldingUpdateRequest("AAPL", "Apple", 10, 150.0);
        response = new HoldingGainDetailsResponse(); // You can fill this with dummy data if needed
    }

    @Test
    void testAddHolding_Success() throws Exception {
        Mockito.when(holdingService.addHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenReturn(response);

        mockMvc.perform(post("/holdings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockName\":\"AAPL\",\"stockSymbol\":\"Apple\",\"quantity\":10,\"buyPrice\":150.0}")
                        .with(csrf())) // ← Add this line
                .andExpect(status().isCreated());
    }

    @Test
    void testAddHolding_UserNotFound() throws Exception {
        Mockito.when(holdingService.addHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(post("/holdings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateHolding_Success() throws Exception {
        Mockito.when(holdingService.updateHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenReturn(response);

        mockMvc.perform(put("/holdings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateHolding_NotFound() throws Exception {
        Mockito.when(holdingService.updateHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenThrow(new NoSuchElementException("Holding not found"));

        mockMvc.perform(put("/holdings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHolding_Success() throws Exception {
        Mockito.when(holdingService.deleteHolding(anyLong(), anyString()))
                .thenReturn("Deleted successfully");

        mockMvc.perform(delete("/holdings/1/AAPL"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteHolding_NotFound() throws Exception {
        Mockito.when(holdingService.deleteHolding(anyLong(), anyString()))
                .thenThrow(new NoSuchElementException("Holding not found"));

        mockMvc.perform(delete("/holdings/1/AAPL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHoldingsForUser_Success() throws Exception {
        HoldingResponseDTO dto = new HoldingResponseDTO(); // dummy
        Mockito.when(holdingService.getHoldingsForUser(1L)).thenReturn(dto);

        mockMvc.perform(get("/holdings/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetHoldingsForUser_NotFound() throws Exception {
        Mockito.when(holdingService.getHoldingsForUser(1L))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/holdings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStockDetails_Success() throws Exception {
        Mockito.when(holdingService.getAllStockDetailsFromExternalAPI())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/holdings/stocks/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStockDetails_Failure() throws Exception {
        Mockito.when(holdingService.getAllStockDetailsFromExternalAPI())
                .thenThrow(new RuntimeException("API failure"));

        mockMvc.perform(get("/holdings/stocks/all"))
                .andExpect(status().isServiceUnavailable());
    }
}
