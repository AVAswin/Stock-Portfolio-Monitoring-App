package org.cg.stockportfoliomonitoringapp.holding.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDetailsRequest {
	 @NotBlank(message="Stock name cannot be blank")
	 private String name;
	 
	 @NotBlank(message =" Stock symbol cannot be blank")
	 private String symbol;
	 
	 private String sector;
	 
	 @PositiveOrZero(message =" Current Price must  be positive or zero")
	 private Double currentPrice; 
	 
	 
}
