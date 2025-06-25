package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

public record CharacterCreated(String name, long timestamp, PlayerCharacter character)
        implements DomainEvent {
    public static final String TYPE = "CHARACTER_CREATED";

    @Override
    public String getType() {
        return TYPE;
    }

    public CharacterCreated(PlayerCharacter character) {
        this(character.getName(), System.currentTimeMillis(), character);
    }
}