package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.value_objects.Item;

public record ItemRemoved(String name, long timestamp, Item item)
        implements DomainEvent {
    public static final String TYPE = "ITEM_REMOVED";

    public ItemRemoved(String name, Item item) {
        this(name, System.currentTimeMillis(), item);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}