package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

public record CharacterCreated(long timestamp, PlayerCharacter character)
        implements DomainEvent {
    public static final String TYPE = "CHARACTER_CREATED";

    public CharacterCreated(PlayerCharacter character) {
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