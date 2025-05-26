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

    @GetMapping("/login/{userName}")
    public ResponseEntity<User> login(@Valid @PathVariable String userName) {
        return ResponseEntity.ok(userServices.loginUser(userName));
    }
}
