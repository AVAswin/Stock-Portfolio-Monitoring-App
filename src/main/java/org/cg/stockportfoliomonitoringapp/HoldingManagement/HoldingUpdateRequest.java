package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; // Lombok: Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Lombok: Generates a constructor with no arguments

@Data // From Lombok: Creates getters and setters.
@NoArgsConstructor // From Lombok: Creates an empty constructor.
public class HoldingUpdateRequest {
	
	 private String stockName;
    private String stockSymbol; // The stock ticker symbol.
    private Integer quantity; // Quantity (used for POST/PUT).
    private Double buyPrice; // Buy price (used for POST/PUT).

    // For DELETE, only stockSymbol needs to be provided in the request body.
    // For POST/PUT, all three fields are usually provided.
}