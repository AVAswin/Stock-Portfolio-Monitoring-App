package org.cg.stockportfoliomonitoringapp.PortfolioManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddPortfolio_Success() throws Exception {
        Long userId = 1L;
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(100L);
        portfolio.setUserId(userId);
        portfolio.setPortfolioName("Tech Stocks");

        Mockito.when(portfolioService.addPortfolio(any(Portfolio.class))).thenReturn(portfolio);

        mockMvc.perform(post("/portfolio/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.portfolioId").value(100))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.portfolioName").value("Tech Stocks"));
    }

    @Test
    void testGetPortfoliosByUserId_Success() throws Exception {
        Long userId = 1L;
        PortfolioResponse response = new PortfolioResponse();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Portfolios fetched");
        response.setUserId(userId);

        Mockito.when(portfolioService.getPortfoliosByUserId(eq(userId))).thenReturn(response);

        mockMvc.perform(get("/portfolio/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.message").value("Portfolios fetched"));
    }
}
