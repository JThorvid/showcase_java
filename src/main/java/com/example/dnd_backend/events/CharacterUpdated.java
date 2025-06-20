package com.example.dnd_backend.events;

import com.example.dnd_backend.entities.CharacterStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterUpdated extends DomainEvent {
    private final CharacterStats stats;

    public CharacterUpdated(String characterName, CharacterStats stats) {
        super(characterName, "CHARACTER_UPDATED");
        this.stats = stats;
    }
}
