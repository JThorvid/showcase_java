package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.domain.entities.Item;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters/{characterName}")
@AllArgsConstructor
public class InventoryController {
    @GetMapping(path = "/inventory")
    public ResponseEntity<List<Item>> getCharacterInventory(@PathVariable String characterName) {
//        Optional<PlayerCharacterPersistenceDTO> characterPersistenceDTO = characterRepository.findByName(characterName);
//        if (characterPersistenceDTO.isPresent()) {
//            Set<ItemPersistenceDTO> inventory = characterPersistenceDTO.get().getInventory();
//            List<ItemDTO> itemDTOs = inventory.stream()
//                    .map(itemAdapter::toItemDTO)
//                    .toList();
//            return ResponseEntity.ok(itemDTOs);
//        } else {
        return ResponseEntity.notFound().build();
//        }
    }

    @PostMapping(path = "/inventory/{itemName}")
    public ResponseEntity<PlayerCharacter> addItemToInventory(@PathVariable String characterName, @PathVariable String itemName) {
//        Optional<PlayerCharacterPersistenceDTO> characterOpt = characterRepository.findByName(characterName);
//        Optional<ItemPersistenceDTO> itemOpt = itemRepository.findByName(itemName);
//
//        if (characterOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        if (itemOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        PlayerCharacterPersistenceDTO character = characterOpt.get();
//        ItemPersistenceDTO item = itemOpt.get();
//
//        character.addItem(item);
//        PlayerCharacterPersistenceDTO updatedCharacter = characterRepository.save(character);
//
//        return ResponseEntity.ok(adapter.fromPersistenceDTO(updatedCharacter));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/inventory/{itemName}")
    public ResponseEntity<PlayerCharacter> removeItemFromInventory(@PathVariable String characterName, @PathVariable String itemName) {
//        Optional<PlayerCharacterPersistenceDTO> characterOpt = characterRepository.findByName(characterName);
//        Optional<ItemPersistenceDTO> itemOpt = itemRepository.findByName(itemName);
//
//        if (characterOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        if (itemOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        PlayerCharacterPersistenceDTO character = characterOpt.get();
//        ItemPersistenceDTO item = itemOpt.get();
//
//        if (!character.getInventory().contains(item)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(adapter.fromPersistenceDTO(character));
//        }
//
//        character.removeItem(item);
//        PlayerCharacterPersistenceDTO updatedCharacter = characterRepository.save(character);
//
//        return ResponseEntity.ok(adapter.fromPersistenceDTO(updatedCharacter));
        return ResponseEntity.notFound().build();
    }
}
