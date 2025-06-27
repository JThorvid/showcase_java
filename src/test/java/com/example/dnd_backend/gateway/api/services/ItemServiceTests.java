package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.application.ItemManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.events.ItemDestroyed;
import com.example.dnd_backend.domain.events.ItemUpdated;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.errors.ItemAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.ItemIsOwnedException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class ItemServiceTests {
    @MockitoBean
    private ItemManager itemManager;

    @MockitoBean
    private CharacterManager characterManager;

    @MockitoBean
    private CharacterEventStore eventStore;

    @Mock
    private PlayerCharacter character;

    @Autowired
    private ItemService service;

    private final Item item = new Item("Sword", "A sharp sword", 1.5);

    @Test
    void testGetItems() {
        // given an item
        Mockito.when(itemManager.getAll()).thenReturn(List.of(item));
        // when all items are requested
        List<Item> items = service.getItems();
        // then the item is returned in a list
        assertTrue(items.contains(item));
    }

    @Test
    void testGetItem() {
        // given an item
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        // when this item is requested
        Item actual = service.getItem(item.name());
        // then the item is returned
        assertEquals(item, actual);
    }

    @Test
    void testGetItem_notFound() {
        // given no item
        String name = item.name();
        Mockito.when(itemManager.getByName(name)).thenReturn(Optional.empty());
        // when this item is requested
        // then an exception is thrown
        assertThrows(ItemNotFoundException.class, () -> service.getItem(name));
    }

    @Test
    void testCreateItem() {
        // given the item does not exist yet
        Mockito.when(itemManager.exists(item.name())).thenReturn(false);
        // when this item is created
        Item actual = service.createItem(item);
        // then the item is returned
        assertEquals(item, actual);
        // and an Item Created event is sent
        verify(eventStore).sendEvent(any(ItemCreated.class));
    }

    @Test
    void testCreateItem_alreadyExists() {
        // given the item already exists
        Mockito.when(itemManager.exists(item.name())).thenReturn(true);
        // when this item is created
        // then an exception is thrown
        assertThrows(ItemAlreadyExistsException.class, () -> service.createItem(item));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testDeleteItem() {
        // given the item exists and is not owned by anyone
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        Mockito.when(characterManager.getAll()).thenReturn(List.of(character));
        Inventory emptyInventory = new Inventory();
        Mockito.when(character.getInventory()).thenReturn(emptyInventory);
        // when this item is deleted
        assertDoesNotThrow(() -> service.deleteItem(item.name()));
        // then a ItemDestroyed event is sent
        verify(eventStore).sendEvent(any(ItemDestroyed.class));
    }

    @Test
    void testDeleteItem_notFound() {
        // given the item does not exist
        String name = item.name();
        Mockito.when(itemManager.getByName(name)).thenReturn(Optional.empty());
        // when this item is deleted
        // then an exception is thrown
        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(name));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testDeleteItem_owned() {
        // given the item exists, but is owned by someone
        String name = item.name();
        Mockito.when(itemManager.getByName(name)).thenReturn(Optional.of(item));
        Mockito.when(characterManager.getAll()).thenReturn(List.of(character));
        Inventory inventory = new Inventory();
        inventory.add(name, 1);
        Mockito.when(character.getInventory()).thenReturn(inventory);
        // when this item is deleted
        // then an exception is thrown
        assertThrows(ItemIsOwnedException.class, () -> service.deleteItem(name));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateItem() {
        // given the item exists
        Mockito.when(itemManager.exists(item.name())).thenReturn(true);
        // when the item is updated
        Item newItem = new Item(item.name(), item.description(), item.weight() + 1);
        Item actual = service.updateItem(newItem);
        // then the updated item gets returned
        assertEquals(newItem, actual);
        // and an ItemUpdated event is sent
        verify(eventStore).sendEvent(any(ItemUpdated.class));
    }

    @Test
    void testUpdateItem_notFound() {
        // given the item does not exist
        Mockito.when(itemManager.exists(item.name())).thenReturn(false);
        // when the item is updated
        Item newItem = new Item(item.name(), item.description(), item.weight() + 1);
        // then an exception is thrown
        assertThrows(ItemNotFoundException.class, () -> service.updateItem(newItem));
        // and no event is sent
        verifyNoInteractions(eventStore);
    }
}