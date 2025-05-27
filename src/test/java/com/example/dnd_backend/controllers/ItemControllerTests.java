package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.persistence.ItemRepository;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

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
        Set<PlayerCharacterPersistenceDTO> noOwners = new HashSet<>();
        ItemPersistenceDTO swordPersistence = new ItemPersistenceDTO(1L, "Sword", "A sharp sword", 1.5, noOwners);
        ItemPersistenceDTO shieldPersistence = new ItemPersistenceDTO(2L, "Shield", "A sturdy shield", 5.0, noOwners);

        ItemDTO swordDTO = new ItemDTO("Sword", "A sharp sword", 1.5);
        ItemDTO shieldDTO = new ItemDTO("Shield", "A sturdy shield", 5.0);

        when(itemRepository.findAll()).thenReturn(List.of(swordPersistence, shieldPersistence));
        when(adapter.toItemDTO(swordPersistence)).thenReturn(swordDTO);
        when(adapter.toItemDTO(shieldPersistence)).thenReturn(shieldDTO);

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Sword"))
                .andExpect(jsonPath("$[1].name").value("Shield"));
    }

    @Test
    void testGetItem() throws Exception {
        ItemPersistenceDTO swordPersistence = new ItemPersistenceDTO(1L, "Sword", "A sharp sword", 1.5, new HashSet<>());
        ItemDTO swordDTO = new ItemDTO("Sword", "A sharp sword", 1.5);

        when(itemRepository.findByName("Sword")).thenReturn(Optional.of(swordPersistence));
        when(adapter.toItemDTO(swordPersistence)).thenReturn(swordDTO);

        mockMvc.perform(get("/items/Sword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sword"))
                .andExpect(jsonPath("$.description").value("A sharp sword"));
    }

    @Test
    void testGetItem_notFound() throws Exception {
        when(itemRepository.findByName("NonExistentItem")).thenReturn(Optional.empty());

        mockMvc.perform(get("/items/NonExistentItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItem() throws Exception {
        ItemDTO newItemDTO = new ItemDTO("Potion", "A healing potion", 0.5);
        ItemPersistenceDTO potionToSave = new ItemPersistenceDTO(null, "Potion", "A healing potion", 0.5, new HashSet<>());
        ItemPersistenceDTO savedPotion = new ItemPersistenceDTO(1L, "Potion", "A healing potion", 0.5, new HashSet<>());

        when(adapter.fromItemDTO(newItemDTO)).thenReturn(potionToSave);
        when(itemRepository.save(any(ItemPersistenceDTO.class))).thenReturn(savedPotion);
        when(adapter.toItemDTO(savedPotion)).thenReturn(newItemDTO);
        
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Potion\",\"description\":\"A healing potion\",\"weight\":0.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Potion"));
    }
} 