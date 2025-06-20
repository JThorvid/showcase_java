package com.example.dnd_backend.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemAdded extends DomainEvent {
    private final String itemName;
    private final String description;
    private final double weight;

    public ItemAdded(String characterName, String itemName, String description, double weight) {
        super(characterName, "ITEM_ADDED");
        this.itemName = itemName;
        this.description = description;
        this.weight = weight;
    }
}
