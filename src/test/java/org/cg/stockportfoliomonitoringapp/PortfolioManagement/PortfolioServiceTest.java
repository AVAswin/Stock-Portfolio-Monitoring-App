package org.cg.stockportfoliomonitoringapp.PortfolioManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolio = new Portfolio();
        portfolio.setUserId(1L);
        portfolio.setUserId(101L);
        portfolio.setPortfolioName("AAPL");
    }

    @Test
    void addPortfolio() {
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        Portfolio result = portfolioService.addPortfolio(portfolio);

        assertNotNull(result);
        assertEquals("AAPL", result.getPortfolioName());
        verify(portfolioRepository, times(1)).save(portfolio);

    }

    @Test
    void getPortfoliosByUserId_WhenPortfoliosExist() {
        List<Portfolio> mockList = Arrays.asList(portfolio);
        when(portfolioRepository.findByUserId(101L)).thenReturn(mockList);

        PortfolioResponse response = portfolioService.getPortfoliosByUserId(101L);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(200, response.getStatusCode());
        assertEquals(1, response.getResult().size());
        assertEquals("Portfolios fetched successfully.", response.getMessage());
    }

    @Test
    void getPortfoliosByUserId_WhenNoPortfoliosExist() {
        when(portfolioRepository.findByUserId(5L)).thenReturn(Collections.emptyList());

        PortfolioResponse response = portfolioService.getPortfoliosByUserId(5L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getResult().isEmpty());
        assertTrue(response.getMessage().contains("No portfolios found for the user ID: 5"));
    }
}