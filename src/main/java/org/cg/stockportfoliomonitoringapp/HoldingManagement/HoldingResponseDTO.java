package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.AllArgsConstructor; 
import lombok.Data; 
import lombok.NoArgsConstructor; 

import java.util.List;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class HoldingResponseDTO {

    private List<HoldingStatusDTO> holdings; 
    private Double totalPortfolioValue; 
    private String message; 
}