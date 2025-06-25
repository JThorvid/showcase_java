package com.example.dnd_backend.domain.value_objects;

public record Item(
        String name,
        String description,
        double weight
) {
}