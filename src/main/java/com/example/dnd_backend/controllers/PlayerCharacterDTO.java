package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.CharacterStats;
import lombok.Data;

@Data
public class PlayerCharacterDTO {
    private final String name;
    private final CharacterStats stats;

    public PlayerCharacterDTO(String name, CharacterStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public PlayerCharacterDTO(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this(name, new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma));
    }

    public CharacterStats getStats() {
        return stats;
    }

    public PlayerCharacterPersistenceDTO toPersistenceDTO() {
        return new PlayerCharacterPersistenceDTO(name, stats);
    }
}