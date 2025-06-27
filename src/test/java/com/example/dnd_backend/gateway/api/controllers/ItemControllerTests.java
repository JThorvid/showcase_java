package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.errors.ItemAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.ItemIsOwnedException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.api.services.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTests {
    @MockitoBean
    private ItemService service;

    private final Item item = new Item("Staff of power", "super powerful", 8);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItems() throws Exception {
        // given there is an item
        Mockito.when(service.getItems()).thenReturn(List.of(item));
        // when all items are requested
        mockMvc.perform(get("/items"))
                // then it is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(item.name()));
    }

    @Test
    void testGetItems_emptyList() throws Exception {
        // given there are no items
        Mockito.when(service.getItems()).thenReturn(List.of());
        // when all items are requested
        mockMvc.perform(get("/items"))
                // then an empty list is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetItem() throws Exception {
        // given there is an item
        Mockito.when(service.getItem(item.name())).thenReturn(item);
        // when the item is requested
        mockMvc.perform(get("/items/" + item.name()))
                // then it is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()));
    }

    @Test
    void testGetItem_notFound() throws Exception {
        // given the item does not exist yet
        Mockito.when(service.getItem(item.name())).thenThrow(ItemNotFoundException.class);
        // when the item is requested
        mockMvc.perform(get("/items/" + item.name()))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItem() throws Exception {
        // given the item does not exist yet
        Mockito.when(service.createItem(item)).thenReturn(item);
        // when the item is created
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then it is returned
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(item.name()));
    }

    @Test
    void testCreateItem_alreadyExists() throws Exception {
        // given the item already exists
        Mockito.when(service.createItem(item)).thenThrow(ItemAlreadyExistsException.class);
        // when the item is created
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then a "bad request" is returned
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteItem() throws Exception {
        // given the item exists and is not owned
        // when the item is deleted
        mockMvc.perform(delete("/items/" + item.name()))
                // then no errors are returned
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteItem_notFound() throws Exception {
        // given the item does not exist
        Mockito.doThrow(ItemNotFoundException.class).when(service).deleteItem(item.name());
        // when the item is deleted
        mockMvc.perform(delete("/items/" + item.name()))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteItem_isOwned() throws Exception {
        // given the item exists and is owned by someone
        Mockito.doThrow(ItemIsOwnedException.class).when(service).deleteItem(item.name());
        // when the item is deleted
        mockMvc.perform(delete("/items/" + item.name()))
                // then a "bad request" is returned
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItem() throws Exception {
        // given the item exists
        Mockito.when(service.updateItem(item)).thenReturn(item);
        // when the item is updated
        mockMvc.perform(put("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then the new item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()));
    }

    @Test
    void testUpdateItem_notFound() throws Exception {
        // given does not exist
        Mockito.when(service.updateItem(item)).thenThrow(ItemNotFoundException.class);
        // when the item is updated
        mockMvc.perform(put("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then the new item is returned
                .andExpect(status().isNotFound());
    }
}