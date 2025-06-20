package com.example.dnd_backend.entities;

import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterUpdated;
import com.example.dnd_backend.events.DomainEvent;
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
//    private List<ItemDTO> inventory;

    public PlayerCharacter(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this(name, new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma));
    }

    public static PlayerCharacter rehydrate(List<DomainEvent> events) {
        PlayerCharacter pc = new PlayerCharacter();
        for (DomainEvent event : events) {
            pc.apply(event);
        }
        return pc;
    }

    private void apply(DomainEvent event) {
        if (event instanceof CharacterCreated e) {
            this.name = e.getName();
            this.stats = e.getCharacter().getStats();
        } else if (event instanceof CharacterUpdated e) {
            this.name = e.getName();
            this.stats = e.getStats();
        }
    }
}