package com.example.Internship_project.repository;

import com.example.Internship_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String name);
    boolean existsByEmail(String name);
}
