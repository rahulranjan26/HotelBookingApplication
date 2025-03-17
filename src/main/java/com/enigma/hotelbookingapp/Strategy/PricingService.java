package com.enigma.hotelbookingapp.Strategy;


import com.enigma.hotelbookingapp.Entities.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {


    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricing = new BaseStrategy();

        pricing = new SurgePriceStrategy(pricing);
        pricing = new OccupancyPricingStrategy(pricing);
        pricing = new HolidayPricing(pricing);
        pricing = new UrgencyPricing(pricing);

        return pricing.calculatePrice(inventory);


    }


    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        BigDecimal finalPrice = BigDecimal.ZERO;
        for (Inventory inventory : inventoryList) {
            finalPrice = finalPrice.add(calculateDynamicPricing(inventory));
        }
        return finalPrice;
    }
}
