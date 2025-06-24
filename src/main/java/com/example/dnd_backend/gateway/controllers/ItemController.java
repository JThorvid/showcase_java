package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.domain.entities.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    //    private final ItemRepository itemRepository;

    @GetMapping
    public List<Item> getItems() {
//        return StreamSupport.stream(itemRepository.findAll().spliterator(), false)
//                .map(adapter::toItemDTO)
//                .toList();
        return List.of();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Item> getItem(@PathVariable String name) {
//        return itemRepository.findByName(name)
//                .map(adapter::toItemDTO)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
//        ItemPersistenceDTO toSave = adapter.fromItemDTO(itemDTO);
//        ItemPersistenceDTO saved = itemRepository.save(toSave);
//        ItemDTO item = adapter.toItemDTO(saved);
//        return ResponseEntity.ok(item);
        return ResponseEntity.notFound().build();
    }
} 