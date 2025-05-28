package org.cg.stockportfoliomonitoringapp.UserManagement;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.InvalidRequestException;
import org.cg.stockportfoliomonitoringapp.ExceptionManagement.ResourceNotFoundException;
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
            throw new InvalidRequestException("Username already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new InvalidRequestException("Email already exists");
        }
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public UserResponseDTO loginUser(String email, String password) {
        if(!userRepository.existsByEmail(email)){
            throw new ResourceNotFoundException("User not found");
        }
        User user=userRepository.findByEmail(email);
        if(!user.getPassword().equals(password)){
            return new UserResponseDTO(HttpStatus.UNAUTHORIZED,HttpStatus.UNAUTHORIZED.value(), user.getUserId(), "Password incorrect");
        }
        return new UserResponseDTO(HttpStatus.OK,HttpStatus.OK.value(), user.getUserId(), "Logged in successfully");
    }

    public User updateUser(String email,User user) {
        if ((!userRepository.existsByEmail(email))){
            throw new ResourceNotFoundException("User not found");
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
