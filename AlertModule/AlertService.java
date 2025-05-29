package org.cg.stockportfoliomonitoringapp.AlertModule;

import org.cg.stockportfoliomonitoringapp.PortfolioModule.*;
import org.cg.stockportfoliomonitoringapp.UserManagement.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Autowired
    private HoldingRepository holdingRepository;


    @Autowired
    private UserRepository userRepository;
    @Scheduled(cron = "0 */5 * * * *") // every 5 mins
    public String checkAlertsFromDatabase() {
        List<PriceAlert> alerts = priceAlertRepository.findByTriggeredFalse();
        List<Holding> holdings = holdingRepository.findAll();
        for (PriceAlert alert : alerts) {
            for (Holding holding : holdings) {
                if (holding.getSymbol().equalsIgnoreCase(alert.getStockSymbol())) {
                    int currentPrice = holding.getPrice();
                    if (currentPrice < alert.getTargetPrice()) {
                        User user = userRepository.findById(alert.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        String subject = " Stock Alert: " + alert.getStockSymbol();
                        String body = String.format(
                                "Hello %s,\n\nThe stock %s has dropped below your alert price of ₹%d.\nCurrent price: ₹%d\n\n- Stock Alert System",
                                user.getUserName(), alert.getStockSymbol(), alert.getTargetPrice(), currentPrice);
                        alert.setTriggered(true);
                        priceAlertRepository.save(alert);
                        return subject+body;
                    }
                    else return "the stock is above the target price";
                }
            }
        }
        return "check completed";
    }

}

