package org.cg.stockportfoliomonitoringapp.UserManagement;

import org.cg.stockportfoliomonitoringapp.ExceptionManagement.InvalidRequestException;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServicesTest {

    @InjectMocks
    private UserServices userServices;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUserName("john");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testRegisterUserSuccess() {
        when(userRepository.existsByUserName("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userServices.registerUser(testUser);

        assertNotNull(result);
        assertEquals("john", result.getUserName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void testRegisterUserUsernameExists() {
        when(userRepository.existsByUserName("john")).thenReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userServices.registerUser(testUser));

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testRegisterUserEmailExists() {
        when(userRepository.existsByUserName("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userServices.registerUser(testUser));

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);

        UserResponseDTO response = userServices.loginUser("john@example.com", "password123");

        assertEquals(200, response.getStatusCode());
        assertEquals("Logged in successfully", response.getMessage());
        assertEquals(testUser.getUserId(), response.getUserId());
    }

    @Test
    void testLoginUserUserNotFound() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userServices.loginUser("unknown@example.com", "any"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLoginUserWrongPassword() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);

        UserResponseDTO response = userServices.loginUser("john@example.com", "wrongpass");

        assertEquals(401, response.getStatusCode());
        assertEquals("Password incorrect", response.getMessage());
    }

    @Test
    void testUpdateUserSuccess() {
        User updateRequest = new User();
        updateRequest.setUserName("johnny");
        updateRequest.setPassword("newpass");
        updateRequest.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(updateRequest);

        User updatedUser = userServices.updateUser("john@example.com", updateRequest);

        assertEquals("johnny", updatedUser.getUserName());
        assertEquals("newpass", updatedUser.getPassword());
    }

    @Test
    void testUpdateUserUserNotFound() {
        when(userRepository.existsByEmail("missing@example.com")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userServices.updateUser("missing@example.com", new User()));
    }
}
