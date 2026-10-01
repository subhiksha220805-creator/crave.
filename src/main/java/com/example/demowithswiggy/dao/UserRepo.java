package com.example.demowithswiggy.dao;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demowithswiggy.model.*;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}


