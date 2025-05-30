package org.cg.stockportfoliomonitoringapp.UserManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private HttpStatus status;
    private int statusCode;
    private long userId;
    private String message;
}
