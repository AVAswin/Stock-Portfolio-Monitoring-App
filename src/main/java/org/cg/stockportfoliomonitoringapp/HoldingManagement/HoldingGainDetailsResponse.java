package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; 
import lombok.NoArgsConstructor; 

@Data 
@NoArgsConstructor 
public class HoldingGainDetailsResponse {

    private Long holdingId; 
    private String stockName; 
    private String stockSymbol; 
    private Integer quantity; 
    private Double buyPrice; 
    private Double currentPrice; 
    private Double totalBuyValue; 
    private Double currentMarketValue; 
    private Double profitOrLoss; 
    private Double gainPercent; 
    private String sector;
    private String message; 

 
}