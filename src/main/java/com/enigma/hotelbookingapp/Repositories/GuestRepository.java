package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}