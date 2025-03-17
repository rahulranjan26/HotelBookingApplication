package com.enigma.hotelbookingapp.Strategy;

import com.enigma.hotelbookingapp.Entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
