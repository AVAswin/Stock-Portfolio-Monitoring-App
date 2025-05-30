package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.Data;
import lombok.NoArgsConstructor; 

@Data 
@NoArgsConstructor 
public class HoldingUpdateRequest {
	
	 private String stockName;
    private String stockSymbol; 
    private Integer quantity; 
    private Double buyPrice; 

  
}