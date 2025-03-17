package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.Booking;
import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {




    List<Hotel> findByOwner(User user);
}
