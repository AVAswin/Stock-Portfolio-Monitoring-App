package org.cg.stockportfoliomonitoringapp.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    @Autowired
    private final UserRepository userRepository;
    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User registerUser(User user) {
        if(userRepository.existsByUserName(user.getUserName())){
            throw new RuntimeException("Username already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public UserResponse loginUser(String email,String password) {
        if(!userRepository.existsByEmail(email)){
            throw new RuntimeException("email not found");
        }
        User user=userRepository.findByEmail(email);
        if(!user.getPassword().equals(password)){
            return new UserResponse(HttpStatus.UNAUTHORIZED, user.getUserId(), "Password incorrect");
        }
        return new UserResponse(HttpStatus.OK, user.getUserId(), "Logged in successfully");
    }

    public User updateUser(String email,User user) {
        if ((!userRepository.existsByEmail(email))){
            throw new RuntimeException("Email not found");
        }
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (user.getUserName() != null) {
            existingUser.setUserName(user.getUserName());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        return userRepository.save(existingUser);
    }
}
