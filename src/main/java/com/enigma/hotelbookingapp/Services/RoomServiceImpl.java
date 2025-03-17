package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.DTO.RoomDTO;
import com.enigma.hotelbookingapp.Entities.*;
import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import com.enigma.hotelbookingapp.Exceptions.UnAuthorisedException;
import com.enigma.hotelbookingapp.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final PhotoRepository photoRepository;
    private final AmnetiesRepository amnetiesRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @Override
    public RoomDTO createRoom(Long hotelId, RoomDTO roomDTO) {
        log.info("Creating  Room with roomId {} in hotel with hotelId {}", roomDTO.getRoomId(), hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with hotel Id : " + hotelId + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        Room room = modelMapper.map(roomDTO, Room.class);
        log.info("Saving the room {}", roomDTO.getRoomId());
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        savePhotoAndAmneties(savedRoom, roomDTO.getPhotos(), roomDTO.getAmenities());

        if (hotel.getActive())
            inventoryService.initializeRoomInventory(room);


        return modelMapper.map(savedRoom, RoomDTO.class);
    }

    @Override
    public RoomDTO  updateRoom(Long hotelId, RoomDTO roomDTO, Long roomId) {
        log.info("Updating Room with roomId {} in hotel with hotelId {}", roomDTO.getRoomId(), hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with hotel Id : " + hotelId + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        Room room = hotel.getRooms().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Room with id : " + roomId + " not found"));
        modelMapper.map(roomDTO, room);
        room.setRoomId(roomId);
        room.setHotel(hotel);
        return modelMapper.map(roomRepository.save(room), RoomDTO.class);

    }

    @Override
    public void deleteRoom(Long hotelId, Long roomId) {
        log.info("Deleting Room with roomId {} in hotel with hotelId {}", roomId, hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with hotel Id : " + hotelId + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room with id : " + roomId + " not found"));

        room.getAmenities().forEach(a -> amnetiesRepository.deleteById(a.getAmnetieId()));
//        amnetiesRepository.deleteAll(room.getAmenities());
        room.getPhotos().forEach(p -> photoRepository.deleteById(p.getPhotoId()));
//        photoRepository.deleteAll(room.getPhotos());
        inventoryRepository.deleteByRoom(room);
        roomRepository.delete(room);
    }

    @Override
    public RoomDTO getRoom(Long hotelId, Long roomId) {
        log.info("Getting a  Room with roomId {} in hotel with hotelId {}", roomId, hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with hotel Id : " + hotelId + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        Room room = hotel.getRooms().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Room with id : " + roomId + " not found"));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getRooms(Long hotelId) {
        log.info("Getting all the  Room in hotel with hotelId {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with hotel Id : " + hotelId + " not found")
        );
        return hotel.getRooms().stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());
    }


    public void savePhotoAndAmneties(Room room, List<Photos> pic, List<Amneties> amneties) {
        pic.forEach(photo -> {
            photo.setRoom(room);
            photoRepository.save(photo);
        });

        amneties.forEach(amnety -> {
            amnety.setRoom(room);
            amnetiesRepository.save(amnety);
        });
    }
}
