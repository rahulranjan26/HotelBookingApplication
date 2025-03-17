package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.HotelDTO;
import com.enigma.hotelbookingapp.DTO.HotelReportDTO;
import com.enigma.hotelbookingapp.Entities.*;
import com.enigma.hotelbookingapp.Entities.Enums.Role;
import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import com.enigma.hotelbookingapp.Exceptions.UnAuthorisedException;
import com.enigma.hotelbookingapp.Repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final PhotoRepository photoRepository;
    private final AmnetiesRepository amnetiesRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating a new Hotel wiht name : {}", hotelDTO.getName());
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive(false);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);


        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created with name : {}", savedHotel.getName());
        log.info("Saving the Photos and Amneties in the respective db schemas");
        savePhotoAndAmneties(savedHotel, hotelDTO.getPhotos(), hotelDTO.getAmenities());
        HotelDTO hotelDTOSaved = modelMapper.map(savedHotel, HotelDTO.class);
        return hotelDTOSaved;
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        log.info("Retrieving Hotel with id : {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with id: " + id + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");

        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotel(Long hotelId, HotelDTO hotelDTO) {
        log.info("Updating Hotel with id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with id: " + hotelId + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        modelMapper.typeMap(HotelDTO.class, Hotel.class).addMappings(mapper -> {
            mapper.skip(Hotel::setPhotos);
            mapper.skip(Hotel::setAmenities);
        });

        modelMapper.map(hotelDTO, hotel);
        log.info("Saving the updated hotel with id : {}", hotelId);
        hotel.setHotelId(hotelId);
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel updated with name : {}", savedHotel.getName());
        return modelMapper.map(savedHotel, HotelDTO.class);
    }

    @Override
    public Boolean deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with id: " + id + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        for (Room room : hotel.getRooms()) {
            inventoryRepository.deleteByRoom(room);
            roomRepository.delete(room);
        }
        hotelRepository.delete(hotel);
        return true;
    }

    @Override
    public void activateHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Hotel with id: " + id + " not found")
        );
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("The owner is different");
        hotel.setActive(true);
        hotelRepository.save(hotel);
        for (Room room : hotel.getRooms())
            inventoryService.initializeRoomInventory(room);

    }

    @Override
    public List<HotelDTO> getAllHotels() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getRoles().contains(Role.ADMIN))
            throw new UnAuthorisedException("The User " + user.getName() + " is not an admin");
        log.info("We are getting the hotels managed by owner : {}", user);
        List<Hotel> hotel = hotelRepository.findByOwner(user);
        return hotel.stream().map((element) -> modelMapper.map(element, HotelDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getAllBookings(Long hotelId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getRoles().contains(Role.ADMIN))
            throw new UnAuthorisedException("The User " + user.getName() + " is not an admin");
        log.info("We are getting the hotels managed by owner : {}", user);
        List<Booking> bookings = bookingRepository.findByHotel(hotelId);

        return bookings.stream().map(element -> modelMapper.map(element, BookingDTO.class)).collect(Collectors.toList());
    }




    public void savePhotoAndAmneties(Hotel hotel, List<Photos> pic, List<Amneties> amneties) {
        pic.stream().forEach(photo -> {
            photo.setHotel(hotel);
            photoRepository.save(photo);
        });

        amneties.stream().forEach(amnety -> {
            amnety.setHotel(hotel);
            amnetiesRepository.save(amnety);
        });
    }


}

