package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.DomainEvent;

import java.util.List;

public interface CharacterRepository {
    List<PlayerCharacter> getAllCharacters();

    void sendEvent(DomainEvent event);
}
