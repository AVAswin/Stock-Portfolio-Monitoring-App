package org.cg.stockportfoliomonitoringapp.HoldingManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HoldingControllerTest {

    private HoldingService holdingService;
    private HoldingController holdingController;

    @BeforeEach
    void setUp() {
        holdingService = mock(HoldingService.class);
        holdingController = new HoldingController(holdingService);
    }

    @Test
    void testAddHolding_Success() {
        HoldingUpdateRequest request = new HoldingUpdateRequest();
        request.setStockSymbol("AAPL");
        request.setStockName("Apple Inc.");
        request.setQuantity(10);
        request.setBuyPrice(150.0);

        HoldingGainDetailsResponse response = new HoldingGainDetailsResponse();
        response.setStockSymbol("AAPL");
        response.setMessage("Holding successfully added.");

        when(holdingService.addHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenReturn(response);

        ResponseEntity<?> result = holdingController.addHolding(1L, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof HoldingGainDetailsResponse);
        assertEquals("AAPL", ((HoldingGainDetailsResponse) result.getBody()).getStockSymbol());
    }

    @Test
    void testAddHolding_IllegalArgumentException() {
        HoldingUpdateRequest request = new HoldingUpdateRequest();
        request.setStockSymbol("AAPL");
        request.setStockName("Apple Inc.");
        request.setQuantity(10);
        request.setBuyPrice(150.0);

        when(holdingService.addHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Conflict"));

        ResponseEntity<?> result = holdingController.addHolding(1L, request);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Conflict", result.getBody());
    }


    @Test
    void testUpdateHolding_NotFound() {
        HoldingUpdateRequest request = new HoldingUpdateRequest();
        request.setStockSymbol("GOOG");
        request.setStockName("Google");
        request.setQuantity(5);
        request.setBuyPrice(1000.0);

        when(holdingService.updateHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenThrow(new NoSuchElementException("Not Found"));

        ResponseEntity<?> result = holdingController.updateHolding(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Not Found", result.getBody());
    }

    @Test
    void testDeleteHolding_Success() {
        when(holdingService.deleteHolding(1L, "MSFT"))
                .thenReturn("Deleted successfully");

        ResponseEntity<String> result = holdingController.deleteHolding(1L, "MSFT");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Deleted successfully", result.getBody());
    }

    @Test
    void testGetHoldingsForUser_Success() {
        HoldingResponseDTO responseDTO = new HoldingResponseDTO(Collections.emptyList(), 0.0, "Success");

        when(holdingService.getHoldingsForUser(1L)).thenReturn(responseDTO);

        ResponseEntity<?> response = holdingController.getHoldingsForUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetAllStockDetails_RuntimeException() {
        HoldingUpdateRequest request = new HoldingUpdateRequest();
        request.setStockSymbol("AAPL");
        request.setStockName("Apple Inc.");
        request.setQuantity(10);
        request.setBuyPrice(150.0);

        when(holdingService.addHolding(anyLong(), anyString(), anyString(), anyInt(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Conflict"));

        ResponseEntity<?> result = holdingController.addHolding(1L, request);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Conflict", result.getBody());
    }

}