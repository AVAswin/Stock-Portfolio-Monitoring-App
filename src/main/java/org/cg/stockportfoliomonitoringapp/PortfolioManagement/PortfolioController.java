package org.cg.stockportfoliomonitoringapp.PortfolioManagement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping("/{userId}")
    public ResponseEntity<Portfolio> addPortfolio(@Valid @PathVariable Long userId,@Valid @RequestBody Portfolio portfolio)
    {
        portfolio.setUserId(userId);
        Portfolio saved = portfolioService.addPortfolio(portfolio);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PortfolioResponse> getPortfoliosByUserId(@Valid @PathVariable Long userId) {
        PortfolioResponse portfolios = portfolioService.getPortfoliosByUserId(userId);
        return new ResponseEntity<>(portfolios, portfolios.getStatus());
    }
}