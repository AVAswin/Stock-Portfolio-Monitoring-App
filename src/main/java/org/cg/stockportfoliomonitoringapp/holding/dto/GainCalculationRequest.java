package org.cg.stockportfoliomonitoringapp.holding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive; // Ensure price and quantity are positive

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GainCalculationRequest {

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotBlank(message = "Stock symbol cannot be empty")
    private String stockSymbol;

    @NotNull(message = "Current price cannot be null")
    @Positive(message = "Current price must be positive")
    private Double currentPrice;
}
