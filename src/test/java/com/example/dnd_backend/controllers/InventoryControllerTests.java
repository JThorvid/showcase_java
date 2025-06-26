package com.example.dnd_backend.controllers;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.application.ItemManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.ItemAdded;
import com.example.dnd_backend.domain.events.ItemRemoved;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.controllers.InventoryController;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(InventoryController.class)
class InventoryControllerTests {
    @MockitoBean
    private CharacterEventStore eventStore;

    @MockitoBean
    private CharacterManager characterManager;

    @MockitoBean
    private ItemManager itemManager;

    @MockitoBean
    private PlayerCharacter character;

    private InventoryController controller;
    private final Item item = new Item("Staff of power", "super powerful", 8);
    private final Inventory inventory = new Inventory();

    @BeforeEach
    void setUp() {
        controller = new InventoryController(eventStore, characterManager, itemManager);
        inventory.add(item);
        Mockito.when(character.getName()).thenReturn("Alice");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacterInventory() {
        // given a character with an item
        Mockito.when(character.getInventory()).thenReturn(inventory);
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when the inventory is requested
        ResponseEntity<Inventory> response = controller.getCharacterInventory(character.getName());
        // then the item is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventory, response.getBody());
    }

    @Test
    void testGetCharacterInventory_characterNotFound() {
        // given no character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.empty());
        // when the inventory is requested
        ResponseEntity<Inventory> response = controller.getCharacterInventory(character.getName());
        // the a "not found is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddItemToInventory() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when an existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        controller.addItemToInventory(character.getName(), item.name());
        // then an ItemAdded event is sent
        verify(eventStore).sendEvent(any(ItemAdded.class));
    }

    @Test
    void testAddItemToInventory_characterNotFound() {
        // given a non-existing character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.empty());
        // when an existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        ResponseEntity<Object> response = controller.addItemToInventory(character.getName(), item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testAddItemToInventory_itemNotFound() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when a non-existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        ResponseEntity<Object> response = controller.addItemToInventory(character.getName(), item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testRemoveItemFromInventory() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when an existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        controller.removeItemFromInventory(character.getName(), item.name());
        // then an ItemRemoved event is sent
        verify(eventStore).sendEvent(any(ItemRemoved.class));
    }

    @Test
    void testRemoveItemFromInventory_characterNotFound() {
        // given a non-existing character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.empty());
        // when an existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        ResponseEntity<Object> response = controller.removeItemFromInventory(character.getName(), item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testRemoveItemFromInventory_itemNotFound() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when a non-existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        ResponseEntity<Object> response = controller.removeItemFromInventory(character.getName(), item.name());
        // then a "not found" is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // and no events are sent
        verifyNoInteractions(eventStore);
    }
}
