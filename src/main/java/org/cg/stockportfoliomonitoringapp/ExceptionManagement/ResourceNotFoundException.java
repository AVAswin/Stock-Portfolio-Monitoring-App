package org.cg.stockportfoliomonitoringapp.ExceptionManagement;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
