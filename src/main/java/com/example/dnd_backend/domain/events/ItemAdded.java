package com.example.dnd_backend.domain.events;

import com.example.dnd_backend.domain.value_objects.Item;

public record ItemAdded(String name, long timestamp, Item item, int quantity)
        implements DomainEvent {
    public static final String TYPE = "ITEM_ADDED";

    public ItemAdded(String name, Item item, int quantity) {
        this(name, System.currentTimeMillis(), item, quantity);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}