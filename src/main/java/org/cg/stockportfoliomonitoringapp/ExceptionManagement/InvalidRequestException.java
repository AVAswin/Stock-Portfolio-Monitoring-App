package org.cg.stockportfoliomonitoringapp.ExceptionManagement;

public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message) {
        super(message);
    }
}

