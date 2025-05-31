package org.cg.stockportfoliomonitoringapp.UserManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServicesTest {

    private UserRepository userRepository;
    private UserServices userServices;
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userServices = new UserServices(userRepository);
    }

    @Test
    void registerUser() {
        User user = new User();
        user.setUserName("Aman");
        user.setEmail("amanyadav@gmail.com");
        user.setPassword("aman1234");
        when(userRepository.existsByUserName("Aman")).thenReturn(false);
        when(userRepository.existsByEmail("amanyadav@gmail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userServices.registerUser(user);
        assertEquals("Aman", result.getUserName());
    }

    @Test
    void loginUser() {
        User user = new User();
        user.setEmail("amanyadav@gmail.com");
        user.setPassword("aman1234");
        user.setUserId(1L);
        when(userRepository.existsByEmail("amanyadav@gmail.com")).thenReturn(true);
        when(userRepository.findByEmail("amanyadav@gmail.com")).thenReturn(user);

        UserResponseDTO response = userServices.loginUser("amanyadav@gmail.com", "aman1234");
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateUser() {
        String email = "amanyadav@gmail.com";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setUserName("Aman");
        existingUser.setPassword("aman1234");

        User updateRequest = new User();
        updateRequest.setEmail(email);
        updateRequest.setUserName("Aman yadav");
        updateRequest.setPassword("aman12345");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updatedUser = userServices.updateUser(email, updateRequest);

        assertEquals("Aman yadav", updatedUser.getUserName());
        assertEquals("aman12345", updatedUser.getPassword());

    }
}