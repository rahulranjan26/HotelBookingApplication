package com.enigma.hotelbookingapp.Strategy;


import com.enigma.hotelbookingapp.Entities.Inventory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;


public class BaseStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
