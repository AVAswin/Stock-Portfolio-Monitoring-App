package org.cg.stockportfoliomonitoringapp.UserManagement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @NotEmpty(message = "userName cannot be null")
    @Column(unique = true, nullable = false)
    private String userName;
    @NotEmpty(message = "Email cannot be null")
    @Column(unique = true, nullable = false)
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid email format"
    )
    private String email;
    @NotEmpty(message = "password is mandatory")
    @Column(nullable = false)
    @Size(min = 6,message = "password should not be less than 6 words")
    private String password;

    public User(){}
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

}
