package org.cg.stockportfoliomonitoringapp.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User loginUser(String userName) {
        if(!userRepository.existsByUserName(userName)){
            throw new RuntimeException("Username not found");
        }
        User user=userRepository.findByUserName(userName);
        return user;
    }
}
