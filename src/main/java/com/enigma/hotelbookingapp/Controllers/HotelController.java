package com.enigma.hotelbookingapp.Controllers;


import com.enigma.hotelbookingapp.Advices.APIResponse;
import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.HotelDTO;
import com.enigma.hotelbookingapp.DTO.HotelReportDTO;
import com.enigma.hotelbookingapp.Services.BookingService;
import com.enigma.hotelbookingapp.Services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(path = "/admin/hotels")
@RestController
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDTO> addHotel(@RequestBody HotelDTO hotelDTO) {
        log.info("Adding a hotel with Id : {}" + hotelDTO.getName());
        HotelDTO hotel = hotelService.createHotel(hotelDTO);
        return new ResponseEntity(hotel, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDTO> getHotel(@PathVariable Long hotelId) {
        log.info("Getting hotel with Id : {}" + hotelId);
        return new ResponseEntity(hotelService.getHotelById(hotelId), HttpStatus.OK);
    }

    @PutMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDTO) {
        log.info("Updating hotel with Id : {}" + hotelId);
        HotelDTO hotel = hotelService.updateHotel(hotelId, hotelDTO);
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{hotelId}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long hotelId) {
        log.info("Deleting hotel with Id : {}" + hotelId);
        hotelService.deleteHotel(hotelId);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PatchMapping(path = "/{hotelId}")
    public ResponseEntity<Boolean> activateHotel(@PathVariable Long hotelId) {
        log.info("Activating hotel with Id : {}" + hotelId);
        hotelService.activateHotel(hotelId);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());

    }

    @GetMapping(path = "/{hotelId}/bookings")
    public ResponseEntity<List<BookingDTO>> getHotelBookings(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getAllBookings(hotelId));
    }


    @GetMapping(path = "/{hotelId}/reports")
    public ResponseEntity<HotelReportDTO> getReports(@PathVariable Long hotelId,
                                                     @RequestParam(required = false) LocalDateTime startDate,
                                                     @RequestParam(required = false) LocalDateTime endDate) {
        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null)
            endDate = LocalDateTime.now();
        return ResponseEntity.ok(bookingService.getReports(hotelId, startDate, endDate));
    }

}
