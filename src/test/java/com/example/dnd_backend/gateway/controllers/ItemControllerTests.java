package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.application.ItemManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.events.ItemDestroyed;
import com.example.dnd_backend.domain.events.ItemUpdated;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(ItemController.class)
class ItemControllerTests {
    @MockitoBean
    private ItemManager itemManager;

    @MockitoBean
    private CharacterManager characterManager;

    @MockitoBean
    private CharacterEventStore eventStore;

    private ItemController controller;

    @MockitoBean
    private PlayerCharacter character;

    private final Item item = new Item("Sword", "A sharp sword", 1.5);

    @BeforeEach
    void setUp() {
        controller = new ItemController(eventStore, itemManager, characterManager);
    }

    @Test
    void testGetItems() {
        // given an item
        Mockito.when(itemManager.getAll()).thenReturn(List.of(item));
        // when all items are requested
        List<Item> items = controller.getItems();
        // then the item is returned in a list
        assertTrue(items.contains(item));
    }

    @Test
    void testGetItem() {
        // given an item
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        // when this item is requested
        ResponseEntity<Item> response = controller.getItem(item.name());
        // then the item is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item, response.getBody());
    }

    @Test
    void testGetItem_notFound() {
        // given no item
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        // when this item is requested
        ResponseEntity<Item> response = controller.getItem(item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateItem() {
        // given the item does not exist yet
        Mockito.when(itemManager.exists(item.name())).thenReturn(false);
        // when this item is created
        ResponseEntity<Item> response = controller.createItem(item);
        // then the item is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item, response.getBody());
        // and an Item Created event is sent
        verify(eventStore).sendEvent(any(ItemCreated.class));
    }

    @Test
    void testCreateItem_alreadyExists() {
        // given the item already exists
        Mockito.when(itemManager.exists(item.name())).thenReturn(true);
        // when this item is created
        ResponseEntity<Item> response = controller.createItem(item);
        // then a "bad request" is returned
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        controller.deleteItem(item.name());
        // then a ItemDestroyed event is sent
        verify(eventStore).sendEvent(any(ItemDestroyed.class));
    }

    @Test
    void testDeleteItem_notFound() {
        // given the item does not exists
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        // when this item is deleted
        ResponseEntity<Object> response = controller.deleteItem(item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testDeleteItem_owned() {
        // given the item exists, but is owned by someone
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        Mockito.when(characterManager.getAll()).thenReturn(List.of(character));
        Inventory inventory = new Inventory();
        inventory.add(item.name(), 1);
        Mockito.when(character.getInventory()).thenReturn(inventory);
        // when this item is deleted
        ResponseEntity<Object> response = controller.deleteItem(item.name());
        // then a "bad request" is returned
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateItem() {
        // given the item exists
        Mockito.when(itemManager.exists(item.name())).thenReturn(true);
        // when the item is updated
        Item newItem = new Item(item.name(), item.description(), item.weight() + 1);
        ResponseEntity<Item> response = controller.updateItem(newItem);
        // then the updated item gets returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newItem, response.getBody());
        // and an ItemUpdated event is sent
        verify(eventStore).sendEvent(any(ItemUpdated.class));
    }

    @Test
    void testUpdateItem_notFound() {
        // given the item does not exist
        Mockito.when(itemManager.exists(item.name())).thenReturn(false);
        // when the item is updated
        Item newItem = new Item(item.name(), item.description(), item.weight() + 1);
        ResponseEntity<Item> response = controller.updateItem(newItem);
        // then a "not found" gets returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no event is sent
        verifyNoInteractions(eventStore);
    }
}