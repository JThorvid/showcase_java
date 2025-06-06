package com.example.dnd_backend.events;

import com.example.dnd_backend.controllers.CharacterStatsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterUpdated extends CharacterEvent {
    private final CharacterStatsDTO stats;

    public CharacterUpdated(String characterName, CharacterStatsDTO stats) {
        super(characterName, "CHARACTER_UPDATED");
        this.stats = stats;
    }
}
