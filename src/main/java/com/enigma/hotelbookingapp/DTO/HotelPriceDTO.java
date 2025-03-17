package com.enigma.hotelbookingapp.DTO;

import com.enigma.hotelbookingapp.Entities.Hotel;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDTO {

    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private Double price;
}
