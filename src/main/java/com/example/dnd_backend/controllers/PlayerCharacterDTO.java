package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.CharacterStats;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record PlayerCharacterDTO(
    String name,
    @JsonUnwrapped
    CharacterStats stats
) {
    public PlayerCharacterDTO(String name, CharacterStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public PlayerCharacterDTO(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this(name, new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma));
    }

    public int strength() {
        return stats.getStrength();
    }

    public int dexterity() {
        return stats.getDexterity();
    }

    public int constitution() {
        return stats.getConstitution();
    }

    public int intelligence() {
        return stats.getIntelligence();
    }

    public int wisdom() {
        return stats.getWisdom();
    }

    public int charisma() {
        return stats.getCharisma();
    }

    public PlayerCharacterPersistenceDTO toPersistenceDTO() {
        return new PlayerCharacterPersistenceDTO(name(), stats);
    }
}