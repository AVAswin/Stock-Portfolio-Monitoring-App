package org.cg.stockportfoliomonitoringapp.PortfolioManagement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
    private List<Portfolio> result;
    private HttpStatus status;
    private int statusCode;
    private String message;
}