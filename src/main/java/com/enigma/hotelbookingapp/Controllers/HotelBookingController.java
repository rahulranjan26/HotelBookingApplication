package com.enigma.hotelbookingapp.Controllers;


import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.BookingRequest;
import com.enigma.hotelbookingapp.DTO.GuestDTO;
import com.enigma.hotelbookingapp.DTO.HotelDTO;
import com.enigma.hotelbookingapp.Services.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bookings")
@RestController
@AllArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping(path = "/init")
    public ResponseEntity<BookingDTO> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping(path = "/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guests) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guests));
    }


    @PostMapping(path = "/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayments(@PathVariable Long bookingId) {
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }


    @PostMapping(path = "/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }


}
