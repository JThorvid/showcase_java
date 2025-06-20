package com.example.dnd_backend.controllers;

import com.example.dnd_backend.entities.ItemDTO;
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
    public List<ItemDTO> getItems() {
//        return StreamSupport.stream(itemRepository.findAll().spliterator(), false)
//                .map(adapter::toItemDTO)
//                .toList();
        return List.of();
    }

    @GetMapping("/{name}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable String name) {
//        return itemRepository.findByName(name)
//                .map(adapter::toItemDTO)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
//        ItemPersistenceDTO toSave = adapter.fromItemDTO(itemDTO);
//        ItemPersistenceDTO saved = itemRepository.save(toSave);
//        ItemDTO item = adapter.toItemDTO(saved);
//        return ResponseEntity.ok(item);
        return ResponseEntity.notFound().build();
    }
} 