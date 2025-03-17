package com.enigma.hotelbookingapp.DTO;

import com.enigma.hotelbookingapp.Entities.Enums.BookingStatus;
import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.Room;
import com.enigma.hotelbookingapp.Entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
public class BookingDTO {
    private Long bookingId;
    private Integer roomCount;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDTO> guests;

}
