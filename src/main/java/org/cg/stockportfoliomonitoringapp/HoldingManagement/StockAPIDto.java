package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data; 
import lombok.NoArgsConstructor; 

@Data 
@NoArgsConstructor 
public class StockAPIDto { 

    private String symbol; 
    private String name; 
    private double currentPrice; 
    private String sector;

}