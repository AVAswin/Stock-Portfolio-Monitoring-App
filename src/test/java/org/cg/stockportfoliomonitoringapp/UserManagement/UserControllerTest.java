package org.cg.stockportfoliomonitoringapp.UserManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.InvalidRequestException;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServices userServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUserName("john");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        Mockito.when(userServices.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userName").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("john@example.com", "password123");
        UserResponseDTO responseDTO = new UserResponseDTO(
                org.springframework.http.HttpStatus.OK, 200, 1L, "Logged in successfully"
        );

        Mockito.when(userServices.loginUser("john@example.com", "password123")).thenReturn(responseDTO);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Logged in successfully"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        String email = "john@example.com";
        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setUserName("johnny");
        updatedUser.setEmail(email);
        updatedUser.setPassword("newpass");

        Mockito.when(userServices.updateUser(eq(email), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/user/update/" + email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("johnny"))
                .andExpect(jsonPath("$.email").value(email));
    }

}
