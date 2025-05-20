package com.example.dnd_backend.controllers;

public record PlayerCharacterDTO(String name, int strength, int dexterity, int constitution, int wisdom,
                                 int intelligence,
                                 int charisma) {
}