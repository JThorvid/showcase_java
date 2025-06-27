package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.dtos.ItemCommandDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDtoAdapter;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.errors.InventoryCorruptException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.api.services.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc
class InventoryControllerTests {
    @MockitoBean
    private InventoryService service;
    @MockitoBean
    private ItemDtoAdapter adapter;
    @Autowired
    private ObjectMapper objectMapper;

    private final String characterName = "Alice";
    private final Inventory inventory = new Inventory();
    private final Item item = new Item("Staff of power", "super powerful", 8);
    private final int quantity = 1;
    private final ItemCommandDTO itemCommandDTO = new ItemCommandDTO(item.name(), quantity);

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        inventory.add(item.name(), 1);
        Mockito.when(adapter.itemToCommandDto(item, quantity)).thenReturn(itemCommandDTO);
    }

    @Test
    void testGetCharacterInventory() throws Exception {
        // given a character with an item
        ItemDTO itemDTO = new ItemDtoAdapter().itemToDto(item, quantity);
        Mockito.when(service.getInventoryFor(characterName)).thenReturn(List.of(itemDTO));

        // when the inventory is requested
        mockMvc.perform(get("/characters/{characterName}/inventory", characterName))
                // then the item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(item.name()))
                .andExpect(jsonPath("$[0].quantity").value(quantity));
    }

    @Test
    void testGetCharacterInventory_characterNotFound() throws Exception {
        // given no character
        Mockito.when(service.getInventoryFor(characterName)).thenThrow(new CharacterNotFoundException(characterName));

        // when the inventory is requested
        mockMvc.perform(get("/characters/{characterName}/inventory", characterName))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCharacterInventory_corruptInventory() throws Exception {
        // given a character with a non-existent item in inventory
        Mockito.when(service.getInventoryFor(characterName)).thenThrow(new InventoryCorruptException(""));

        // when the inventory is requested
        mockMvc.perform(get("/characters/{characterName}/inventory", characterName))
                // then an "internal server error" is returned
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testAddItemToInventory() throws Exception {
        // given the character exists and has the item and the item exists
        Mockito.when(service.addToInventoryFor(characterName, itemCommandDTO)).thenReturn(itemCommandDTO);
        // when the post request is done
        mockMvc.perform(post("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then the item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()))
                .andExpect(jsonPath("$.quantity").value(quantity));
    }

    @Test
    void testAddItemToInventory_characterNotFound() throws Exception {
        // given no character
        Mockito.when(service.addToInventoryFor(characterName, itemCommandDTO)).thenThrow(new CharacterNotFoundException(characterName));
        // when the post request is done
        mockMvc.perform(post("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddItemToInventory_itemNotFound() throws Exception {
        // given no item
        Mockito.when(service.addToInventoryFor(characterName, itemCommandDTO)).thenThrow(new ItemNotFoundException(itemCommandDTO.name()));

        // when the post request is done
        mockMvc.perform(post("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromInventory() throws Exception {
        // given a character which has the item and the item exists
        Mockito.when(service.removeFromInventoryFor(characterName, itemCommandDTO)).thenReturn(itemCommandDTO);
        // when the delete request is done
        mockMvc.perform(delete("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then the item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()))
                .andExpect(jsonPath("$.quantity").value(quantity));
    }

    @Test
    void testRemoveItemFromInventory_characterNotFound() throws Exception {
        // given no character
        Mockito.when(service.removeFromInventoryFor(characterName, itemCommandDTO)).thenThrow(new CharacterNotFoundException(characterName));

        // when the delete request is done
        mockMvc.perform(delete("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromInventory_itemNotFound() throws Exception {
        // given no item
        Mockito.when(service.removeFromInventoryFor(characterName, itemCommandDTO)).thenThrow(ItemNotFoundException.class);

        // when the delete request is done
        mockMvc.perform(delete("/characters/{characterName}/inventory", characterName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCommandDTO)))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }
}
