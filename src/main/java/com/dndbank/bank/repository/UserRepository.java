package com.dndbank.bank.repository;

import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(Role role);
    Optional<User> findByRole(Role role);
}
