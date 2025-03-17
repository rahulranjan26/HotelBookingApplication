package com.enigma.hotelbookingapp.DTO;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@ToString
@Getter
@Setter
@NoArgsConstructor
public class HotelSearchRequest {
    private String city;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer roomsCount;
    private Integer page = 0;
    private Integer size = 10;

}
