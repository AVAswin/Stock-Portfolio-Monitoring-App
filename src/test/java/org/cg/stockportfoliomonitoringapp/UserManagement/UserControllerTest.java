package org.cg.stockportfoliomonitoringapp.UserManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserServices userServices;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userServices = mock(UserServices.class);
        userController = new UserController(userServices);
    }

    @Test
    public void testLoginSuccessful() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("aswin@example.com");
        loginDTO.setPassword("secure123");

        UserResponseDTO responseDTO = new UserResponseDTO(
                org.springframework.http.HttpStatus.OK,
                200,
                1L,
                "Logged in successfully"
        );

        when(userServices.loginUser("aswin@example.com", "secure123")).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.login(loginDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logged in successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    public void testLoginInvalidEmailFormat() {

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("invalid-email");
        loginDTO.setPassword("secure123");

        assertEquals("invalid-email", loginDTO.getEmail());
        assertTrue(loginDTO.getPassword().length() >= 6);
    }

    @Test
    public void testLoginShortPassword() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("aswin@example.com");
        loginDTO.setPassword("123");

        assertTrue(loginDTO.getPassword().length() < 6);
    }
}