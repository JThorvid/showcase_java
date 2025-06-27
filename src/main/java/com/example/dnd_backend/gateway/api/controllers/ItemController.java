package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.errors.ItemAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.ItemIsOwnedException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.api.services.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    ItemService itemService;

    @GetMapping
    public List<Item> getItems() {
        return itemService.getItems();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> getItem(@PathVariable String name) {
        try {
            Item item = itemService.getItem(name);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody Item item) {
        try {
            Item createdItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (ItemAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateItem(@RequestBody Item item) {
        try {
            Item updatedItem = itemService.updateItem(item);
            return ResponseEntity.ok(updatedItem);
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Object> deleteItem(@PathVariable String name) {
        try {
            itemService.deleteItem(name);
            return ResponseEntity.ok().build();
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ItemIsOwnedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}