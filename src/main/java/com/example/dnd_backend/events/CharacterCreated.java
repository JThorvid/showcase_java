package com.example.dnd_backend.events;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterCreated extends CharacterEvent {
    private final PlayerCharacterDTO character;

    public CharacterCreated(PlayerCharacterDTO character) {
        super(character.name(), "CHARACTER_CREATED");
        this.character = character;
    }
}
