package com.enigma.hotelbookingapp.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryDTO {
    private BigDecimal surgeFactor;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean closed;
}
