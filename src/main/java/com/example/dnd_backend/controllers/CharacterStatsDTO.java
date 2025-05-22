package com.example.dnd_backend.controllers;

public record CharacterStatsDTO(
    int strength,
    int dexterity,
    int constitution,
    int intelligence,
    int wisdom,
    int charisma
) {} 