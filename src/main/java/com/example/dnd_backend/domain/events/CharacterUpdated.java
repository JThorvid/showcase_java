package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterUpdated extends DomainEvent {
    private final PlayerCharacter character;
    public static final String TYPE = "CHARACTER_UPDATED";

    // required for serialization
    public CharacterUpdated() {
        super("", TYPE);
        this.character = new PlayerCharacter();
    }

    public CharacterUpdated(PlayerCharacter playerCharacter) {
        super(playerCharacter.getName(), TYPE);
        this.character = playerCharacter;
    }
}
