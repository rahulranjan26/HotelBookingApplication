package com.enigma.hotelbookingapp.DTO;

import com.enigma.hotelbookingapp.Entities.Amneties;
import com.enigma.hotelbookingapp.Entities.HotelContactInfo;
import com.enigma.hotelbookingapp.Entities.Photos;
import com.enigma.hotelbookingapp.Entities.Room;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class HotelDTO {
    private Long hotelId;
    private String name;
    private String city;
    private List<Photos> photos;
    private List<Amneties> amenities;
    private HotelContactInfo hotelContactInfo;
    private Boolean active;
    private List<Room> rooms;
}
