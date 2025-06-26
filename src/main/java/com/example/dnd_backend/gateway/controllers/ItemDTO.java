package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.domain.value_objects.Item;

public record ItemDTO(String name, String description, double weight, int amount) {
    public ItemDTO(Item item, int amount) {
        this(item.name(), item.description(), item.weight(), amount);
    }
}
