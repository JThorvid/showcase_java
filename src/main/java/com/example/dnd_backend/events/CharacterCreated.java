package com.example.dnd_backend.events;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterCreated extends CharacterEvent {
    private final PlayerCharacterPersistenceDTO character;

    public CharacterCreated(PlayerCharacterPersistenceDTO character) {
        super(character.getName(), "CHARACTER_CREATED");
        this.character = character;
    }
}
