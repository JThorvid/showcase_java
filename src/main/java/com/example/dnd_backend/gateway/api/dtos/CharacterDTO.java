package com.example.dnd_backend.gateway.api.dtos;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.value_objects.CharacterStats;

public record CharacterDTO(
        String name,
        int strength,
        int dexterity,
        int constitution,
        int intelligence,
        int wisdom,
        int charisma
) {
    public PlayerCharacter toEntity() {
        return new PlayerCharacter(
                name,
                new CharacterStats(
                        strength,
                        dexterity,
                        constitution,
                        intelligence,
                        wisdom,
                        charisma
                )
        );
    }
}
