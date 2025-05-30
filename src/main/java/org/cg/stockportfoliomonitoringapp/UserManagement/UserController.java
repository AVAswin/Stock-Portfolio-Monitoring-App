package org.cg.stockportfoliomonitoringapp.UserManagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "1. User",description = "User Management APIs")
public class UserController {
    @Autowired
    private  final UserServices userServices;
    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userServices.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        UserResponseDTO response=userServices.loginUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(@Valid @PathVariable String email, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userServices.updateUser(email,user));
    }
}
