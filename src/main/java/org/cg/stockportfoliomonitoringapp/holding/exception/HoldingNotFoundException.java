package org.cg.stockportfoliomonitoringapp.holding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) 
public class HoldingNotFoundException extends RuntimeException {
    public HoldingNotFoundException(String message) {
        super(message);
    }

    public HoldingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}