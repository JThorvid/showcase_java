package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

public record ItemAdded(String name, long timestamp, PlayerCharacter character)
        implements DomainEvent {
    public static final String TYPE = "ITEM_ADDED";

    @Override
    public String getType() {
        return TYPE;
    }

    public ItemAdded(PlayerCharacter character) {
        this(character.getName(), System.currentTimeMillis(), character);
    }
}