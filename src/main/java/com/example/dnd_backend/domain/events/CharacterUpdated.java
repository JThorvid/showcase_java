package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

public record CharacterUpdated(long timestamp, PlayerCharacter character)
        implements DomainEvent {
    public static final String TYPE = "CHARACTER_UPDATED";

    public CharacterUpdated(PlayerCharacter character) {
        this(System.currentTimeMillis(), character);
    }

    @Override
    public String name() {
        return character.getName();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}