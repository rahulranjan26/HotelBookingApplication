package com.enigma.hotelbookingapp.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDTO {

    private Long totalBookings;
    private BigDecimal totalBookingAmount;
    private BigDecimal averageAmountPerBooking;
}
