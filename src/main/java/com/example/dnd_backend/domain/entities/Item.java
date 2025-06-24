package com.example.dnd_backend.domain.entities;

public record Item(
        String name,
        String description,
        double weight
) {
}