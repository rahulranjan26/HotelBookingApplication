package com.enigma.hotelbookingapp.Services;


import com.enigma.hotelbookingapp.DTO.RoomDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoomService {

    RoomDTO createRoom(Long hotelId, RoomDTO roomDTO);

    RoomDTO updateRoom(Long hotelId, RoomDTO roomDTO,Long roomId);

    void deleteRoom(Long hotelId, Long roomId);

    RoomDTO getRoom(Long hotelId, Long roomId);

    List<RoomDTO> getRooms(Long hotelId);
}
