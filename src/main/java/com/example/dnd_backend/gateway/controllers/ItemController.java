package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.EventRepository;
import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.events.ItemDestroyed;
import com.example.dnd_backend.domain.events.ItemUpdated;
import com.example.dnd_backend.domain.value_objects.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final EventRepository eventStore;
    private Projector<Item> itemManager;
    private Projector<PlayerCharacter> characterManager;

    @GetMapping
    public List<Item> getItems() {
        return itemManager.getAll();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Item> getItem(@PathVariable String name) {
        Optional<Item> item = itemManager.getByName(name);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        if (itemManager.exists(item.name())) {
            return ResponseEntity.badRequest().build();
        } else {
            eventStore.sendEvent(new ItemCreated(item));
            return ResponseEntity.ok().body(item);
        }
    }

    @PutMapping
    public ResponseEntity<Item> updateItem(@RequestBody Item item) {
        if (itemManager.exists(item.name())) {
            eventStore.sendEvent(new ItemUpdated(item));
            return ResponseEntity.ok().body(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Object> deleteItem(@PathVariable String name) {
        Item item = itemManager.getByName(name).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        List<PlayerCharacter> owningCharacters = charactersThatOwnThisItem(item);
        if (!owningCharacters.isEmpty()) {
            String message = String.format("This item is currently owned by these characters: %s\n" +
                            "You have to remove the item from their inventories before you can proceed.",
                    owningCharacters.stream().map(PlayerCharacter::getName).toList());
            return ResponseEntity.badRequest().body(message);
        }
        eventStore.sendEvent(new ItemDestroyed(item));
        return ResponseEntity.ok().build();
    }

    private List<PlayerCharacter> charactersThatOwnThisItem(Item item) {
        List<PlayerCharacter> characters = new ArrayList<>();
        for (PlayerCharacter player : characterManager.getAll()) {
            if (player.getInventory().getCountPerItem().containsKey(item))
                characters.add(player);
        }
        return characters;
    }
}