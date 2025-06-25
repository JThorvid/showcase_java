package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

public record CharacterUpdated(String name, long timestamp, PlayerCharacter character)
        implements DomainEvent {
    public static final String TYPE = "CHARACTER_UPDATED";

    @Override
    public String getType() {
        return TYPE;
    }

    public CharacterUpdated(PlayerCharacter character) {
        this(character.getName(), System.currentTimeMillis(), character);
    }
}