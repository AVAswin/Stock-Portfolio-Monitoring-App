package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; 
import lombok.NoArgsConstructor; 

@Data 
@NoArgsConstructor 
public class HoldingStatusDTO {

    private String symbol; 
    private String companyName; 
    private Integer quantity; 
    private Double buyPrice; 
    private Double currentPrice; 
    private Double profitOrLoss; 
    private Double gainPercentage; 
    private String sector; 
}