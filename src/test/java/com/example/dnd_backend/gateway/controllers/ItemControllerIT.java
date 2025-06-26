package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.domain.value_objects.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerIT {
    @MockitoBean
    private ItemController itemController;

    private final Item item = new Item("Staff of power", "super powerful", 8);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItems() throws Exception {
        // given there is an item
        Mockito.when(itemController.getItems()).thenReturn(List.of(item));
        // when all items are requested
        mockMvc.perform(get("/items"))
                // then it is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(item.name()));
    }

    @Test
    void testGetItem() throws Exception {
        // given there is an item
        Mockito.when(itemController.getItem(item.name())).thenReturn(ResponseEntity.ok(item));
        // when the item is requested
        mockMvc.perform(get("/items/" + item.name()))
                // then it is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()));
    }

    @Test
    void testCreateItem() throws Exception {
        // given there the item creation succeeds
        Mockito.when(itemController.createItem(item)).thenReturn(ResponseEntity.ok(item));
        // when the item is created
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then it is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()));
    }

    @Test
    void testDeleteItem() throws Exception {
        // given there the item deletion succeeds
        Mockito.when(itemController.deleteItem(item.name())).thenReturn(ResponseEntity.ok().build());
        // when the item is deleted
        mockMvc.perform(delete("/items/" + item.name()))
                // then no errors are returned
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateItem() throws Exception {
        // given the update item succeeds
        Mockito.when(itemController.updateItem(item)).thenReturn(ResponseEntity.ok(item));
        // when the item is updated
        mockMvc.perform(put("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                // then the new item is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.name()));
    }
}