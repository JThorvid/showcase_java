package com.example.dnd_backend.domain.aggregates;

import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacter {
    private String name;
    private CharacterStats stats;

    public void apply(DomainEvent event) {
        if (event instanceof CharacterUpdated e) {
            this.name = e.name();
            this.stats = e.character().getStats();
        }
    }
}