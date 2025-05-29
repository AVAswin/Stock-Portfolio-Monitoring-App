package org.cg.stockportfoliomonitoringapp.holding.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedHoldingResponse {
    private String stockName;
    private String stockSymbol;
    private Integer quantity;
    private Double buyPrice;
    private Double currentPrice; // The current price used for calculation (from Xano or user)
    private Double calculatedGain;
    private Double calculatedGainPercent;
    private String message; // E.g., "Stock data found on Xano", "Stock not found on Xano"
}