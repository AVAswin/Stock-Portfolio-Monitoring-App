package org.cg.stockportfoliomonitoringapp.PortfolioManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private PortfolioRepository portfolioRepository;

    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(100L);
        testPortfolio.setPortfolioName("Tech Stocks");
    }

    @Test
    void testAddPortfolio_Success() {
        when(portfolioRepository.save(testPortfolio)).thenReturn(testPortfolio);

        Portfolio saved = portfolioService.addPortfolio(testPortfolio);

        assertNotNull(saved);
        assertEquals(1L, saved.getPortfolioId());
        verify(portfolioRepository, times(1)).save(testPortfolio);
    }

    @Test
    void testGetPortfoliosByUserId_Found() {
        List<Portfolio> portfolioList = List.of(testPortfolio);
        when(portfolioRepository.findByUserId(100L)).thenReturn(portfolioList);

        PortfolioResponse response = portfolioService.getPortfoliosByUserId(100L);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.getResult().size());
        assertEquals("Portfolios fetched successfully.", response.getMessage());
    }

    @Test
    void testGetPortfoliosByUserId_NotFound() {
        when(portfolioRepository.findByUserId(200L)).thenReturn(Collections.emptyList());

        PortfolioResponse response = portfolioService.getPortfoliosByUserId(200L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals(0, response.getResult().size());
        assertEquals("No portfolios found for the user ID: 200", response.getMessage());
    }
}
