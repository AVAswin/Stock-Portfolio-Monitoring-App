package org.cg.stockportfoliomonitoringapp.PortfolioManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepository;
    public Portfolio addPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    public PortfolioResponse getPortfoliosByUserId(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        if (portfolios.isEmpty()) {
            return new PortfolioResponse(portfolios, HttpStatus.NOT_FOUND,HttpStatus.NOT_FOUND.value(), "No portfolios found for the user ID: " + userId);
        } else {
            return new PortfolioResponse(portfolios, HttpStatus.OK,HttpStatus.OK.value(), "Portfolios fetched successfully.");
        }
    }
}