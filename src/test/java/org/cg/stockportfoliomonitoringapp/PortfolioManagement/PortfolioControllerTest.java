package org.cg.stockportfoliomonitoringapp.PortfolioManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioControllerTest {

    private PortfolioController portfolioController;
    private PortfolioService portfolioService;

    @BeforeEach
    public void setUp() throws Exception {
        portfolioController = new PortfolioController();
        portfolioService = mock(PortfolioService.class);

        Field serviceField = PortfolioController.class.getDeclaredField("portfolioService");
        serviceField.setAccessible(true);
        serviceField.set(portfolioController, portfolioService);
    }

    @Test
    public void testAddPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(1L);

        when(portfolioService.addPortfolio(portfolio)).thenReturn(portfolio);

        var response = portfolioController.addPortfolio(1L, portfolio);

        assertEquals(portfolio, response.getBody());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void testGetPortfoliosByUserId() {
        Long userId = 1L;
        List<Portfolio> portfolios = List.of(new Portfolio());
        PortfolioResponse expectedResponse = new PortfolioResponse(portfolios, HttpStatus.OK, 200, "Success");

        when(portfolioService.getPortfoliosByUserId(userId)).thenReturn(expectedResponse);

        var response = portfolioController.getPortfoliosByUserId(userId);

        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}



