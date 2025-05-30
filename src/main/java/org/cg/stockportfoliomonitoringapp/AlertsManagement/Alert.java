package org.cg.stockportfoliomonitoringapp.AlertsManagement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotBlank(message = "Symbol is required")
    private String symbol;
    @NotBlank(message = "TargetPrice is required")
    private String TargetPrice;
    private String gainOrLoss;

}
