package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/holdings")
@Tag(name = "3. Holding",description = "Holding Management APIs")
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> addHolding(
            @PathVariable Long userId,
            @RequestBody HoldingUpdateRequest request) { 
        try {
            HoldingGainDetailsResponse response = holdingService.addHolding(
                    userId,
                    request.getStockSymbol(),
                    request.getStockName(), 
                    request.getQuantity(),
                    request.getBuyPrice()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
   
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateHolding(
            @PathVariable Long userId,
            @RequestBody HoldingUpdateRequest request) { 
        try {
            HoldingGainDetailsResponse response = holdingService.updateHolding(
                    userId,
                    request.getStockSymbol(),
                    request.getStockName(), 
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