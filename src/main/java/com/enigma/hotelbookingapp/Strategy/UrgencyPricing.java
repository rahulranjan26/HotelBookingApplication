package com.enigma.hotelbookingapp.Strategy;

import com.enigma.hotelbookingapp.Entities.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UrgencyPricing implements PricingStrategy {

    PricingStrategy wrapper;

    public UrgencyPricing(PricingStrategy wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapper.calculatePrice(inventory);

        LocalDateTime today = LocalDateTime.now();

        if (inventory.getDate().isBefore(today.plusDays(7))) {
            price = price.multiply(BigDecimal.valueOf(1.8));
        }
        return price;
    }
}
