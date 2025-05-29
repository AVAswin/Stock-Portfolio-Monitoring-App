package org.cg.stockportfoliomonitoringapp.holding.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedHoldingRequest {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be positive")
    private Long userId; // User ID (though not used for lookup in this specific task)

    @NotBlank(message = "Stock name cannot be empty")
    private String name; // Stock name for comparison with Xano data

    @NotBlank(message = "Stock symbol cannot be empty")
    private String symbol; // Stock symbol for comparison with Xano data

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // User's buy quantity

    @NotNull(message = "Buy price cannot be null")
    @Min(value = 0, message = "Buy price cannot be negative")
    private Double buyPrice; // User's buy price for simulation

    // Optional: User can provide a current price if Xano doesn't have it or to override
    private Double userProvidedCurrentPrice;
}