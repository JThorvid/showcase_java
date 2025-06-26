package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.EventRepository;
import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.events.ItemAdded;
import com.example.dnd_backend.domain.events.ItemRemoved;
import com.example.dnd_backend.domain.value_objects.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/characters/{characterName}")
@AllArgsConstructor
public class InventoryController {
    private final EventRepository eventStore;
    private Projector<PlayerCharacter> characterManager;
    private Projector<Item> itemManager;

    @GetMapping(path = "/inventory")
    public ResponseEntity<Inventory> getCharacterInventory(@PathVariable String characterName) {
        Optional<PlayerCharacter> character = characterManager.getByName(characterName);
        return character
                .map(pc -> new ResponseEntity<>(pc.getInventory(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/inventory/{itemName}")
    public ResponseEntity<Object> addItemToInventory(@PathVariable String characterName, @PathVariable String itemName) {
        return processItemRequest(characterName, itemName, AddOrRemove.ADD);
    }

    @DeleteMapping(path = "/inventory/{itemName}")
    public ResponseEntity<Object> removeItemFromInventory(@PathVariable String characterName, @PathVariable String itemName) {
        return processItemRequest(characterName, itemName, AddOrRemove.REMOVE);
    }

    private ResponseEntity<Object> processItemRequest(String characterName, String itemName, AddOrRemove addOrRemove) {
        Optional<PlayerCharacter> character = characterManager.getByName(characterName);
        if (character.isPresent()) {
            Optional<Item> item = itemManager.getByName(itemName);
            if (item.isPresent()) {
                DomainEvent event;
                switch (addOrRemove) {
                    case ADD -> event = new ItemAdded(characterName, item.get());
                    case REMOVE -> event = new ItemRemoved(characterName, item.get());
                    default -> throw new IllegalArgumentException("Unknown operation: " + addOrRemove);
                }
                eventStore.sendEvent(event);
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The item does not exist");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The character does not exist");
        }
    }
}

