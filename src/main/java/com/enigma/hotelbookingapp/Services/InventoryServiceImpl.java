package com.enigma.hotelbookingapp.Services;


import com.enigma.hotelbookingapp.DTO.*;
import com.enigma.hotelbookingapp.Entities.Inventory;
import com.enigma.hotelbookingapp.Entities.Room;
import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import com.enigma.hotelbookingapp.Repositories.HotelMinPriceRepository;
import com.enigma.hotelbookingapp.Repositories.InventoryRepository;
import com.enigma.hotelbookingapp.Repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;


    @Override
    public void initializeRoomInventory(Room room) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime updateTime = today.plusYears(1);

        for (; !today.isAfter(updateTime); today = today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .price(room.getBasePrice())
                    .reservedCount(0)
                    .bookedCount(0)
                    .surgeFactor(BigDecimal.valueOf(1))
                    .date(today)
                    .city(room.getHotel().getCity())
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);

        }

    }

    @Override
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        System.out.println(hotelSearchRequest);
        log.info("Searching hotels in city: {}, from {} to {}, roomsCount: {}, dateCount: {}",
                hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(), dateCount);
        Page<HotelPriceDTO> hotels = hotelMinPriceRepository.findHotelBySearchParams(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable
        );
        log.info("Hotels found: {}", hotels.getTotalElements());
        return hotels;
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFoundException("room with room Id : " + roomId + " not found")
        );
        if (!room.getHotel().getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new ResourceNotFoundException("Room is not owned by the owner:");
        }
        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element) -> modelMapper.map(element, InventoryDTO.class))
                .collect(Collectors.toList());


    }


    @Override
    public void updateTheRepositoryInBetweenDate(Long roomId, UpdateInventoryDTO updateInventoryDTO) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFoundException("Room with room Id : " + roomId + " not found")
        );
        if (!room.getHotel().getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new ResourceNotFoundException("Room is not owned by the owner:");
        }

        inventoryRepository.findAndLockUpdateInventory(roomId, updateInventoryDTO.getStartDate(), updateInventoryDTO.getEndDate(), updateInventoryDTO.getClosed());
        inventoryRepository.findAndUpdateLockInventory(roomId,
                updateInventoryDTO.getStartDate(),
                updateInventoryDTO.getEndDate(),
                updateInventoryDTO.getClosed(),
                updateInventoryDTO.getSurgeFactor());

    }


}
