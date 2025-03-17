package com.enigma.hotelbookingapp.Strategy;

import com.enigma.hotelbookingapp.Entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;


public class OccupancyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapper;

    public OccupancyPricingStrategy(PricingStrategy wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapper.calculatePrice(inventory);

        double occupancy = inventory.getBookedCount() / inventory.getTotalCount();

        if (occupancy > 0.8)
            price = price.multiply(new BigDecimal(1.5));
        return price;
    }
}
