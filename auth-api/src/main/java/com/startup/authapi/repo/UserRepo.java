package com.startup.authapi.repo;

import com.startup.authapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    java.util.Optional<User> findByEmail(String email);
}