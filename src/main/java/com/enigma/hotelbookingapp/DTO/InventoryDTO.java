package com.enigma.hotelbookingapp.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
    private Long inventoryId;
    private LocalDateTime date;
    private Integer bookedCount;
    private Integer totalCount;
    private Integer reservedCount;
    private BigDecimal surgeFactor;
    private BigDecimal price;
    private Boolean closed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
