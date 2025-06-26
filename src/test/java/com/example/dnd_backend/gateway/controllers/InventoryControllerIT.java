package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import com.example.dnd_backend.domain.value_objects.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerIT {

    @MockitoBean
    private InventoryController controller;

    private final PlayerCharacter character = new PlayerCharacter("Alice", new CharacterStats());
    private final Item item = new Item("Staff of power", "super powerful", 8);
    private final Inventory inventory = new Inventory();

    @BeforeEach
    void setUp() {
        inventory.add(item.name(), 1);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacterInventory() throws Exception {
        // given a character with an item
        Mockito.when(controller.getCharacterInventory(character.getName())).thenReturn(ResponseEntity.ok(inventory));

        // when the inventory is requested
        mockMvc.perform(get("/characters/{characterName}/inventory", character.getName()))
                // then the item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countPerItem").exists())
                .andExpect(jsonPath("$.countPerItem['Staff of power']").value(1));
    }

    @Test
    void testAddItemToInventory() throws Exception {
        // given addItemToInventory succeeds
        Mockito.when(controller.addItemToInventory(character.getName(), item.name())).thenReturn(ResponseEntity.ok().build());
        // when the post request is done
        mockMvc.perform(post("/characters/{characterName}/inventory/{itemName}", character.getName(), item.name()))
                // then no errors are returned
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveItemFromInventory() throws Exception {
        // given removeItemFromInventory succeeds
        Mockito.when(controller.removeItemFromInventory(character.getName(), item.name())).thenReturn(ResponseEntity.ok().build());
        // when the delete request is done
        mockMvc.perform(delete("/characters/{characterName}/inventory/{itemName}", character.getName(), item.name()))
                // then no errors are returned
                .andExpect(status().isOk());
    }
}
