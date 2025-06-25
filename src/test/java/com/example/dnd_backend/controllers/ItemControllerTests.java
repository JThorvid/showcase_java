package com.example.dnd_backend.controllers;

import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.controllers.ItemController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItems() throws Exception {
        Item swordDTO = new Item("Sword", "A sharp sword", 1.5);
        Item shieldDTO = new Item("Shield", "A sturdy shield", 5.0);

//        when(itemRepository.findAll()).thenReturn(List.of(swordPersistence, shieldPersistence));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Sword"))
                .andExpect(jsonPath("$[1].name").value("Shield"));
    }

    @Test
    void testGetItem() throws Exception {
        Item swordDTO = new Item("Sword", "A sharp sword", 1.5);

//        when(itemRepository.findByName("Sword")).thenReturn(Optional.of(swordPersistence));

        mockMvc.perform(get("/items/Sword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sword"))
                .andExpect(jsonPath("$.description").value("A sharp sword"));
    }

    @Test
    void testGetItem_notFound() throws Exception {
//        when(itemRepository.findByName("NonExistentItem")).thenReturn(Optional.empty());

        mockMvc.perform(get("/items/NonExistentItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItem() throws Exception {
        Item newItem = new Item("Potion", "A healing potion", 0.5);

//        when(itemRepository.save(any(ItemPersistenceDTO.class))).thenReturn(savedPotion);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Potion\",\"description\":\"A healing potion\",\"weight\":0.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Potion"));
    }
} 