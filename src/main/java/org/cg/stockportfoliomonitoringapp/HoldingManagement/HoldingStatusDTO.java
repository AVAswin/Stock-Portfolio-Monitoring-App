package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; // Lombok: Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Lombok: Generates a constructor with no arguments

@Data // From Lombok: Creates getters and setters.
@NoArgsConstructor // From Lombok: Creates an empty constructor.
public class HoldingStatusDTO {

    private String symbol; // The stock ticker symbol.
    private String companyName; // The full name of the company.
    private Integer quantity; // The number of shares held.
    private Double buyPrice; // The average buy price.
    private Double currentPrice; // The current market price from the external API.
    private Double profitOrLoss; // Current value minus buy value.
    private Double gainPercentage; // Percentage gain or loss.
    private String sector; // The industry sector.
}