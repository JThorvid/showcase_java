package com.example.dnd_backend.domain.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemRemoved extends DomainEvent {
    private final String itemName;

    public ItemRemoved(String characterName, String itemName) {
        super(characterName, "ITEM_REMOVED");
        this.itemName = itemName;
    }
}
