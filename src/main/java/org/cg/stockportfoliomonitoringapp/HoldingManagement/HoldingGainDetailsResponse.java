package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; // Lombok: Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Lombok: Generates a constructor with no arguments

@Data // From Lombok: Creates getters and setters.
@NoArgsConstructor // From Lombok: Creates an empty constructor.
public class HoldingGainDetailsResponse {

    private Long holdingId; // The ID of the holding in your database.
    private String stockName; // The full name of the company.
    private String stockSymbol; // The stock ticker symbol.
    private Integer quantity; // The number of shares held.
    private Double buyPrice; // The average buy price.
    private Double currentPrice; // The current market price from the external API.
    private Double totalBuyValue; // Quantity * Buy Price.
    private Double currentMarketValue; // Quantity * Current Price.
    private Double profitOrLoss; // Current Market Value - Total Buy Value.
    private Double gainPercent; // (Profit/Loss / Total Buy Value) * 100.
    private String sector; // The industry sector.
    private String message; // A message indicating success or details.

    // Lombok handles the boilerplate.
}