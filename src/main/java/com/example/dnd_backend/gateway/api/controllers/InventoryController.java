package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.gateway.api.dtos.ItemCommandDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDTO;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.errors.InventoryCorruptException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.api.services.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters/{characterName}")
@AllArgsConstructor
public class InventoryController {
    private InventoryService inventoryService;

    @GetMapping(path = "/inventory")
    public ResponseEntity<Object> getCharacterInventory(@PathVariable String characterName) {
        try {
            List<ItemDTO> items = inventoryService.getInventoryFor(characterName);
            return ResponseEntity.ok(items);
        } catch (CharacterNotFoundException | ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InventoryCorruptException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(path = "/inventory")
    public ResponseEntity<Object> addItemToInventory(@PathVariable String characterName, @RequestBody ItemCommandDTO itemCommandDTO) {
        try {
            ItemCommandDTO item = inventoryService.addToInventoryFor(characterName, itemCommandDTO);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException | CharacterNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/inventory")
    public ResponseEntity<Object> removeItemFromInventory(@PathVariable String characterName, @RequestBody ItemCommandDTO itemCommandDTO) {
        try {
            ItemCommandDTO item = inventoryService.removeFromInventoryFor(characterName, itemCommandDTO);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException | CharacterNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

