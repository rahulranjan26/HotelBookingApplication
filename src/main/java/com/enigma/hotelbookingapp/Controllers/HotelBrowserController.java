package com.enigma.hotelbookingapp.Controllers;

import com.enigma.hotelbookingapp.DTO.HotelDTO;
import com.enigma.hotelbookingapp.DTO.HotelPriceDTO;
import com.enigma.hotelbookingapp.DTO.HotelSearchRequest;
import com.enigma.hotelbookingapp.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/hotels/")
@RestController
@RequiredArgsConstructor
public class HotelBrowserController {

    public final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelPriceDTO> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }


}
