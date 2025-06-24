package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterCreated extends DomainEvent {
    private final PlayerCharacter character;
    public static final String TYPE = "CHARACTER_CREATED";

    // required for serialization
    public CharacterCreated() {
        super("", TYPE);
        this.character = new PlayerCharacter();
    }

    public CharacterCreated(PlayerCharacter character) {
        super(character.getName(), TYPE);
        this.character = character;
    }
}
