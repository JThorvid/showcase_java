package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.ItemRepository;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTests {

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private ItemDTOAdapter adapter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItems() throws Exception {
        ItemDTO itemDTO = new ItemDTO("Sword", "A sharp blade", 5.0);
        ItemPersistenceDTO persistenceDTO = new ItemPersistenceDTO(1L, "Sword", "A sharp blade", 5.0);

        when(adapter.toItemDTO(persistenceDTO)).thenReturn(itemDTO);
        when(itemRepository.findAll()).thenReturn(List.of(persistenceDTO));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sword"))
                .andExpect(jsonPath("$[0].description").value("A sharp blade"))
                .andExpect(jsonPath("$[0].weight").value(5.0));
    }

    @Test
    void testGetItem() throws Exception {
        ItemDTO itemDTO = new ItemDTO("Shield", "A sturdy shield", 10.0);
        ItemPersistenceDTO persistenceDTO = new ItemPersistenceDTO(1L, "Shield", "A sturdy shield", 10.0);

        when(adapter.toItemDTO(persistenceDTO)).thenReturn(itemDTO);
        when(itemRepository.findByName("Shield")).thenReturn(Optional.of(persistenceDTO));

        mockMvc.perform(get("/items/Shield"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Shield"))
                .andExpect(jsonPath("$.description").value("A sturdy shield"))
                .andExpect(jsonPath("$.weight").value(10.0));
    }

    @Test
    void testGetItem_notFound() throws Exception {
        when(itemRepository.findByName("NonExistentItem")).thenReturn(Optional.empty());

        mockMvc.perform(get("/items/NonExistentItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItem() throws Exception {
        ItemDTO itemDTO = new ItemDTO("Potion", "A healing potion", 0.5);
        ItemPersistenceDTO persistenceDTOToSave = new ItemPersistenceDTO("Potion", "A healing potion", 0.5);
        ItemPersistenceDTO persistenceDTOSaved = new ItemPersistenceDTO(1L, "Potion", "A healing potion", 0.5);

        when(adapter.fromItemDTO(itemDTO)).thenReturn(persistenceDTOToSave);
        when(itemRepository.save(persistenceDTOToSave)).thenReturn(persistenceDTOSaved);
        when(adapter.toItemDTO(persistenceDTOSaved)).thenReturn(itemDTO);
        
        String itemJson = """
        {
            "name": "Potion",
            "description": "A healing potion",
            "weight": 0.5
        }
        """;

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Potion"))
                .andExpect(jsonPath("$.description").value("A healing potion"))
                .andExpect(jsonPath("$.weight").value(0.5));

        verify(adapter).fromItemDTO(itemDTO);
        verify(itemRepository).save(persistenceDTOToSave);
        verify(adapter).toItemDTO(persistenceDTOSaved);
        verifyNoMoreInteractions(adapter, itemRepository);
    }
} 