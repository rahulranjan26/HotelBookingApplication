package com.enigma.hotelbookingapp.Controllers;


import com.enigma.hotelbookingapp.DTO.InventoryDTO;
import com.enigma.hotelbookingapp.DTO.UpdateInventoryDTO;
import com.enigma.hotelbookingapp.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/admin/inventory")
@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;


    @GetMapping(path = "/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PutMapping(path = "/rooms/{roomId}")
    public ResponseEntity<Void> updateTheInventoryInBetweenDate(@PathVariable Long roomId, @RequestBody UpdateInventoryDTO updateInventoryDTO) {
        inventoryService.updateTheRepositoryInBetweenDate(roomId, updateInventoryDTO);
        return ResponseEntity.noContent().build();
    }
}
