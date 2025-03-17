package com.enigma.hotelbookingapp.DTO;

import com.enigma.hotelbookingapp.Entities.Amneties;
import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.HotelContactInfo;
import com.enigma.hotelbookingapp.Entities.Photos;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class RoomDTO {
    private Long roomId;
    private String type;
    private BigDecimal basePrice;
    private Integer capacity;
    private Integer totalCount;
    private List<Photos> photos;
    private List<Amneties> amenities;
    private Long hotelId;
}
