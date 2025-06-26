package com.example.dnd_backend.domain.aggregates;

import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.events.ItemAdded;
import com.example.dnd_backend.domain.events.ItemRemoved;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerCharacter {
    private String name;
    private CharacterStats stats;
    private final Inventory inventory = new Inventory();

    public PlayerCharacter(String name, CharacterStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public void apply(DomainEvent event) {
        if (event instanceof CharacterUpdated e) {
            this.name = e.name();
            this.stats = e.character().getStats();
        } else if (event instanceof ItemAdded e) {
            this.inventory.add(e.item());
        } else if (event instanceof ItemRemoved e) {
            this.inventory.remove(e.item());
        }
    }
}