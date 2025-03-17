package com.enigma.hotelbookingapp.Strategy;

import com.enigma.hotelbookingapp.Entities.Inventory;
import java.math.BigDecimal;



public class SurgePriceStrategy implements PricingStrategy {

    private final PricingStrategy wrapper;

    public SurgePriceStrategy(PricingStrategy wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapper.calculatePrice(inventory);
        return price.multiply(BigDecimal.valueOf(1.2));
    }
}
