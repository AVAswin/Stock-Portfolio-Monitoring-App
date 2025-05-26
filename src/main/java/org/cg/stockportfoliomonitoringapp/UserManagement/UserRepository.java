package org.cg.stockportfoliomonitoringapp.UserManagement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUserName(String username);
    boolean existsByEmail(String email);
    User findByUserName(String username);
}
