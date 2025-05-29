package org.cg.stockportfoliomonitoringapp.AlertModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/alerts")
public class AlertController {

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Autowired
    private AlertService alertService;

    @PostMapping
    public PriceAlert createAlert(@RequestBody PriceAlert alert) {
        return priceAlertRepository.save(alert);
    }

    @GetMapping
    public ResponseEntity<List<PriceAlert>> getAllAlerts() {
        return ResponseEntity.ok(priceAlertRepository.findAll());
    }

    @PostMapping("/check")
    public String checkAlertsNow() {
        return alertService.checkAlertsFromDatabase();
    }
}

