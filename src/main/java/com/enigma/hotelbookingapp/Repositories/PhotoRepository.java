package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.Photos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photos, Long> {
}
