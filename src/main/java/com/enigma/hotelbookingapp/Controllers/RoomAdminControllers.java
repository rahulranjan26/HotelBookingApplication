package com.enigma.hotelbookingapp.Controllers;


import com.enigma.hotelbookingapp.DTO.RoomDTO;
import com.enigma.hotelbookingapp.Services.RoomService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/admin/hotels/{hotelId}/room")
@RestController
public class RoomAdminControllers {

    private final RoomService roomService;

    public RoomAdminControllers(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@PathVariable Long hotelId, @RequestBody RoomDTO roomDTO) {
        RoomDTO room = roomService.createRoom(hotelId, roomDTO);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRooms(@PathVariable Long hotelId) {
        return new ResponseEntity<>(roomService.getRooms(hotelId), HttpStatus.OK);
    }

    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long hotelId, @PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.getRoom(hotelId, roomId), HttpStatus.OK);
    }

    @PutMapping(path = "/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long hotelId, @RequestBody RoomDTO roomDTO, @PathVariable Long roomId) {
        RoomDTO roomDTO1 = roomService.updateRoom(hotelId, roomDTO, roomId);
        return new ResponseEntity<>(roomDTO1, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{roomId}")
    public ResponseEntity<Boolean> deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId) {
        roomService.deleteRoom(hotelId, roomId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }



}
