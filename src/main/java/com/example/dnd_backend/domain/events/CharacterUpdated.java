package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.entities.CharacterStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterUpdated extends DomainEvent {
    private final CharacterStats stats;
    public static final String TYPE = "CHARACTER_UPDATED";

    public CharacterUpdated(String characterName, CharacterStats stats) {
        super(characterName, TYPE);
        this.stats = stats;
    }
}
