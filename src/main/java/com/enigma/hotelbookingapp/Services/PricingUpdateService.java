package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.HotelMinPrice;
import com.enigma.hotelbookingapp.Entities.Inventory;
import com.enigma.hotelbookingapp.Repositories.HotelMinPriceRepository;
import com.enigma.hotelbookingapp.Repositories.HotelRepository;
import com.enigma.hotelbookingapp.Repositories.InventoryRepository;
import com.enigma.hotelbookingapp.Strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;



//@Scheduled(cron = "0/5 * * * * *")


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final PricingService pricingService;

    //Scheduler to update the Inventory and Hotel Min Price every hour etc

    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {

        int page = 0;
        int batchSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));

            if (hotelPage.isEmpty()) {
                break;
            }

            hotelPage.getContent().forEach(hotel -> updateHotelPrices(hotel));
            page++;

        }

    }

    private void updateHotelPrices(Hotel hotel) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrices(hotel, inventoryList, startDate, endDate);
    }

    private void updateHotelMinPrices(Hotel hotel, List<Inventory> inventoryList, LocalDateTime startDate, LocalDateTime endDate) {
        // Create a map to store the minimum price for each date

        Map<LocalDateTime, BigDecimal> dailyMinPrices = new HashMap<>();

        // Iterate over the inventory list to find the minimum price per date
        for (Inventory inventory : inventoryList) {
            LocalDateTime date = inventory.getDate();
            BigDecimal price = inventory.getPrice();

            // Update the map only if the new price is lower
            if (!dailyMinPrices.containsKey(date) || price.compareTo(dailyMinPrices.get(date)) < 0) {
                dailyMinPrices.put(date, price);
            }
        }

        // List to store updated hotel prices
        List<HotelMinPrice> hotelPrices = new ArrayList<>();

        for (Map.Entry<LocalDateTime, BigDecimal> entry : dailyMinPrices.entrySet()) {
            LocalDateTime date = entry.getKey();
            BigDecimal price = entry.getValue();

            // Fetch existing record or create a new one
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, price, date));

            // Update price if necessary
            hotelMinPrice.setPrice(price);
            hotelPrices.add(hotelMinPrice);
        }

        // Save all updated prices
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryList);
    }
}