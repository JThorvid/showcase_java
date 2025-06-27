package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.application.ItemManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.ItemAdded;
import com.example.dnd_backend.domain.events.ItemRemoved;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.dtos.ItemCommandDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDtoAdapter;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.errors.InventoryCorruptException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class InventoryServiceTests {
    @MockitoBean
    private CharacterEventStore eventStore;

    @MockitoBean
    private CharacterManager characterManager;

    @MockitoBean
    private ItemManager itemManager;

    @Mock
    private PlayerCharacter character;

    private final ItemDtoAdapter adapter = new ItemDtoAdapter();
    @Autowired
    private InventoryService inventoryService;
    private final Item item = new Item("Staff of power", "super powerful", 8);
    private final Inventory inventory = new Inventory();

    @BeforeEach
    void setUp() {
        inventory.add(item.name(), 1);
        Mockito.when(character.getName()).thenReturn("Alice");
    }

    @Test
    void testGetCharacterInventory() {
        // given a character with an item
        Mockito.when(character.getInventory()).thenReturn(inventory);
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        // when the inventory is requested
        List<ItemDTO> inventoryDto = inventoryService.getInventoryFor(character.getName());
        // then the item is returned
        assertEquals(1, inventoryDto.size());
        Approvals.verify(inventoryDto.getFirst());
    }

    @Test
    void testGetCharacterInventory_characterNotFound() {
        // given no character
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.empty());
        // when the inventory is requested
        // then a CharacterNotFoundException is thrown
        assertThrows(CharacterNotFoundException.class, () -> inventoryService.getInventoryFor(name));
    }

    @Test
    void testGetCharacterInventory_corruptInventory() {
        // given a character with a non-existent item in inventory
        String nonExistentItem = "Non-existent Item";
        inventory.add(nonExistentItem, 1);
        Mockito.when(character.getInventory()).thenReturn(inventory);
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.of(character));
        Mockito.when(itemManager.getByName(nonExistentItem)).thenReturn(Optional.empty());
        // when the inventory is requested
        // then an InventoryCorruptException is thrown
        assertThrows(InventoryCorruptException.class, () -> inventoryService.getInventoryFor(name));
    }

    @Test
    void testAddItemToInventory() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when an existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        inventoryService.addToInventoryFor(character.getName(), adapter.itemToCommandDto(item, 1));
        // then an ItemAdded event is sent
        verify(eventStore).sendEvent(any(ItemAdded.class));
    }

    @Test
    void testAddItemToInventory_characterNotFound() {
        // given a non-existing character
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.empty());
        // when an existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        // then a CharacterNotFoundException is thrown
        ItemCommandDTO itemCommandDTO = adapter.itemToCommandDto(item, 1);
        assertThrows(CharacterNotFoundException.class, () -> inventoryService.addToInventoryFor(name, itemCommandDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testAddItemToInventory_itemNotFound() {
        // given a character
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.of(character));
        // when a non-existing item is added to its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        // then an ItemNotFoundException is thrown
        ItemCommandDTO itemCommandDTO = adapter.itemToCommandDto(item, 1);
        assertThrows(ItemNotFoundException.class, () -> inventoryService.addToInventoryFor(name, itemCommandDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testRemoveItemFromInventory() {
        // given a character
        Mockito.when(characterManager.getByName(character.getName())).thenReturn(Optional.of(character));
        // when an existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        inventoryService.removeFromInventoryFor(character.getName(), adapter.itemToCommandDto(item, 1));
        // then an ItemRemoved event is sent
        verify(eventStore).sendEvent(any(ItemRemoved.class));
    }

    @Test
    void testRemoveItemFromInventory_characterNotFound() {
        // given a non-existing character
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.empty());
        // when an existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.of(item));
        // then a CharacterNotFoundException is thrown
        ItemCommandDTO itemCommandDTO = adapter.itemToCommandDto(item, 1);
        assertThrows(CharacterNotFoundException.class, () -> inventoryService.removeFromInventoryFor(name, itemCommandDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testRemoveItemFromInventory_itemNotFound() {
        // given a character
        String name = character.getName();
        Mockito.when(characterManager.getByName(name)).thenReturn(Optional.of(character));
        // when a non-existing item is removed from its inventory
        Mockito.when(itemManager.getByName(item.name())).thenReturn(Optional.empty());
        // then an ItemNotFoundException is thrown
        ItemCommandDTO itemCommandDTO = adapter.itemToCommandDto(item, 1);
        assertThrows(ItemNotFoundException.class, () -> inventoryService.removeFromInventoryFor(name, itemCommandDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }
}
