package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.events.ItemDestroyed;
import com.example.dnd_backend.domain.events.ItemUpdated;
import com.example.dnd_backend.domain.value_objects.Item;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemManagerTests {
    private final ItemManager itemManager = new ItemManager();
    private final Item item = new Item("Staff", "cool staff", 8);

    @Test
    void testProcessEvent_ItemCreated() {
        // given an ItemCreated event
        DomainEvent event = new ItemCreated(item);
        // when the event is processed
        itemManager.processEvent(event);
        // then the itemManager contains the item
        assertTrue(itemManager.exists(item.name()));
    }

    @Test
    void testProcessEvent_ItemUpdated() {
        // given an item exists and an ItemUpdated event comes in
        itemManager.processEvent(new ItemCreated(item));
        Item newItem = new Item(item.name(), item.description(), item.weight() + 1);
        DomainEvent event = new ItemUpdated(newItem);
        // when the event is processed
        itemManager.processEvent(event);
        // then the itemManager contains only the new item
        List<Item> items = itemManager.getAll();
        assertTrue(items.contains(newItem));
        assertFalse(items.contains(item));
    }

    @Test
    void testProcessEvent_ItemDeleted() {
        // given an item exists and an ItemDestroyed event comes in
        itemManager.processEvent(new ItemCreated(item));
        DomainEvent event = new ItemDestroyed(item);
        // when the event is processed
        itemManager.processEvent(event);
        // then the itemManager does not contain the new item any longer
        assertFalse(itemManager.exists(item.name()));
    }
}
