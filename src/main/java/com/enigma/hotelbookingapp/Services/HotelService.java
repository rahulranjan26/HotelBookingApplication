package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.HotelDTO;
import com.enigma.hotelbookingapp.DTO.HotelReportDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface HotelService {

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Long id);

    HotelDTO updateHotel(Long hotelId, HotelDTO hotelDTO);

    Boolean deleteHotel(Long id);

    void activateHotel(Long hotelId);

    List<HotelDTO> getAllHotels();

    List<BookingDTO> getAllBookings(Long hotelId);


}
