package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.ItemRepository;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemDTOAdapter adapter;

    public ItemController(ItemRepository itemRepository, ItemDTOAdapter adapter) {
        this.itemRepository = itemRepository;
        this.adapter = adapter;
    }

    @GetMapping
    public List<ItemDTO> getItems() {
        return StreamSupport.stream(itemRepository.findAll().spliterator(), false)
                .map(adapter::toItemDTO)
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable String name) {
        return itemRepository.findByName(name)
                .map(adapter::toItemDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        ItemPersistenceDTO toSave = adapter.fromItemDTO(itemDTO);
        ItemPersistenceDTO saved = itemRepository.save(toSave);
        ItemDTO item = adapter.toItemDTO(saved);
        return ResponseEntity.ok(item);
    }
} 