package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.value_objects.Item;

public record ItemUpdated(Item item, long timestamp) implements DomainEvent {
    static final String TYPE = "ITEM_UPDATED";

    public ItemUpdated(Item item) {
        this(item, System.currentTimeMillis());
    }

    @Override
    public String name() {
        return item.name();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
