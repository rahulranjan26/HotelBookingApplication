package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
