package com.example.dnd_backend.controllers;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record PlayerCharacterDTO(
    String name,
    @JsonUnwrapped
    CharacterStatsDTO stats
) {
    public PlayerCharacterDTO(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this(name, new CharacterStatsDTO(strength, dexterity, constitution, intelligence, wisdom, charisma));
    }

    public int strength() {
        return stats.strength();
    }

    public int dexterity() {
        return stats.dexterity();
    }

    public int constitution() {
        return stats.constitution();
    }

    public int intelligence() {
        return stats.intelligence();
    }

    public int wisdom() {
        return stats.wisdom();
    }

    public int charisma() {
        return stats.charisma();
    }
}