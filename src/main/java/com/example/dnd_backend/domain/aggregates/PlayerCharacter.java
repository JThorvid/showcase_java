package com.example.dnd_backend.domain.aggregates;

import com.example.dnd_backend.domain.entities.CharacterStats;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacter {
    private String name;
    private CharacterStats stats;

    public PlayerCharacter(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this(name, new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma));
    }

    public void apply(DomainEvent event) {
        if (event instanceof CharacterUpdated e) {
            this.name = e.getName();
            this.stats = e.getCharacter().getStats();
        }
    }
}