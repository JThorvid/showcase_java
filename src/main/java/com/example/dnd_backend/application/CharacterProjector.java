package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;

import java.util.List;
import java.util.Optional;

public interface CharacterProjector {
    Optional<PlayerCharacter> getCharacterByName(String name);

    List<PlayerCharacter> getCharacters();

    boolean characterExists(String name);
}
