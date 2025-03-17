package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserId(Long userId);

    Optional<User> findByEmail(String email);
}