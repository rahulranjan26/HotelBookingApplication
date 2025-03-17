package com.enigma.hotelbookingapp.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
public class BookingRequest {
    private Long hotelId;
    private Long roomId;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer roomsCount;
}
