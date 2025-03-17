package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.BookingRequest;
import com.enigma.hotelbookingapp.DTO.GuestDTO;
import com.enigma.hotelbookingapp.DTO.HotelReportDTO;
import com.stripe.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDTO initialiseBooking(BookingRequest bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guests);

    String initiatePayments(Long bookingId);

    void capturepayment(Event event);

    void cancelBooking(Long bookingId);

    HotelReportDTO getReports(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);
}
