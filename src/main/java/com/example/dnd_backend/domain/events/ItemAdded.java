package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.value_objects.Item;

public record ItemAdded(String name, long timestamp, Item item)
        implements DomainEvent {
    public static final String TYPE = "ITEM_ADDED";

    public ItemAdded(String name, Item item) {
        this(name, System.currentTimeMillis(), item);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}