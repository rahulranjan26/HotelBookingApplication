package com.enigma.hotelbookingapp.Services;


import com.enigma.hotelbookingapp.DTO.HotelPriceDTO;
import com.enigma.hotelbookingapp.DTO.HotelSearchRequest;
import com.enigma.hotelbookingapp.DTO.InventoryDTO;
import com.enigma.hotelbookingapp.DTO.UpdateInventoryDTO;
import com.enigma.hotelbookingapp.Entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    void initializeRoomInventory(Room room);

    Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest);


    List<InventoryDTO> getAllInventoryByRoom(Long roomId);



    void updateTheRepositoryInBetweenDate(Long roomId, UpdateInventoryDTO updateInventoryDTO);
}
