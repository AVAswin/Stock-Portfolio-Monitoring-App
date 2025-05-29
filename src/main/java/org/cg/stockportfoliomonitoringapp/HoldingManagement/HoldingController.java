// src/main/java/org/cg/stockportfoliomonitoringapp/holding/controller/HoldingController.java
package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/holdings")
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;

    /**
     * Adds a new stock holding for a user.
     * Request body includes stockName. URL contains userId.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<?> addHolding(
            @PathVariable Long userId,
            @RequestBody HoldingUpdateRequest request) { // Use the updated DTO
        try {
            HoldingGainDetailsResponse response = holdingService.addHolding(
                    userId,
                    request.getStockSymbol(),
                    request.getStockName(), // Pass stockName from the request
                    request.getQuantity(),
                    request.getBuyPrice()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            // Catches general runtime errors, including API connection issues
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing stock holding for a user.
     * Request body includes stockName. URL contains userId.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateHolding(
            @PathVariable Long userId,
            @RequestBody HoldingUpdateRequest request) { // Use the updated DTO
        try {
            HoldingGainDetailsResponse response = holdingService.updateHolding(
                    userId,
                    request.getStockSymbol(),
                    request.getStockName(), // Pass stockName from the request
                    request.getQuantity(),
                    request.getBuyPrice()
            );
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes a holding for a user.
     * URL contains userId and stockSymbol (as path variable).
     */
    @DeleteMapping("/{userId}/{stockSymbol}")
    public ResponseEntity<String> deleteHolding(
            @PathVariable Long userId,
            @PathVariable String stockSymbol) {
        try {
            String message = holdingService.deleteHolding(userId, stockSymbol);
            return ResponseEntity.ok(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all holdings for a specific user.
     * URL contains userId.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getHoldingsForUser(@PathVariable Long userId) {
        try {
            HoldingResponseDTO response = holdingService.getHoldingsForUser(userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves details of all stocks from the external mock API.
     */
    @GetMapping("/stocks/all")
    public ResponseEntity<?> getAllStockDetails() {
        try {
            List<StockAPIDto> stockDetails = holdingService.getAllStockDetailsFromExternalAPI();
            return ResponseEntity.ok(stockDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}