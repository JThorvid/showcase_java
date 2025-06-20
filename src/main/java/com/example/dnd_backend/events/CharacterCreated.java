package com.example.dnd_backend.events;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterCreated extends DomainEvent {
    private final PlayerCharacterPersistenceDTO character;
    private static final String TYPE = "CHARACTER_CREATED";

    // required for serialization
    public CharacterCreated() {
        super("", TYPE);
        this.character = new PlayerCharacterPersistenceDTO();
    }

    public CharacterCreated(PlayerCharacterPersistenceDTO character) {
        super(character.getName(), TYPE);
        this.character = character;
    }
}
