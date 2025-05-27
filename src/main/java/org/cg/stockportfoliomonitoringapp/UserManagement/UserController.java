package org.cg.stockportfoliomonitoringapp.UserManagement;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
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
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userServices.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(@Valid @PathVariable String email, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userServices.updateUser(email,user));
    }
}
