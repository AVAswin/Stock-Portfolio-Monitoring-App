package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.AllArgsConstructor; // Lombok: Generates a constructor with all arguments
import lombok.Data; // Lombok: Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Lombok: Generates a constructor with no arguments

import java.util.List;

@Data // From Lombok: Creates getters and setters.
@NoArgsConstructor // From Lombok: Creates an empty constructor.
@AllArgsConstructor // From Lombok: Creates a constructor with all fields as arguments. Useful for creating objects easily.
public class HoldingResponseDTO {

    private List<HoldingStatusDTO> holdings; // A list of individual stock holding statuses.
    private Double totalPortfolioValue; // The total current market value of all holdings.
    private String message; // A message indicating success or details.
}