package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@Tag(name = "4. Alert",description = "Alert Management APIs")
public class AlertController {

    @Autowired
    public AlertService alertService;

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public List<Alert> getAlert(){
        return alertRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<AlertDTO> addAlert(@Valid @RequestBody Alert alert){
        AlertDTO alertDTO = alertService.addAlert(alert);
        return new ResponseEntity<>(alertDTO, HttpStatusCode.valueOf(alertDTO.getStatus()));
    }

    @GetMapping("/{userId}")
    public List<Alert> getAlertsByUserId(@Valid @PathVariable Long userId) {
        return alertRepository.findByUserId(userId);
    }
}
