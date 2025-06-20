package com.example.dnd_backend.entities;

public record ItemDTO(
        String name,
        String description,
        double weight
) {
}