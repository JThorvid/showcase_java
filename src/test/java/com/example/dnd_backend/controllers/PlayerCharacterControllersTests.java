package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerCharacterController.class)
class PlayerCharacterControllersTests {
    @MockitoBean
    private CharacterRepository characterRepository;

    @MockitoBean
    private PlayerCharacterDTOAdapter adapter;

    @MockitoBean
    private ItemDTOAdapter itemAdapter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacters() throws Exception {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        when(characterRepository.findAll()).thenReturn(List.of(persistenceDTO));

        mockMvc.perform(get("/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[0].strength").value(18));
    }

    @Test
    void testGetCharacter() throws Exception {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        when(characterRepository.findByName("Bob")).thenReturn(Optional.of(persistenceDTO));

        mockMvc.perform(get("/characters/Bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.strength").value(18));
    }

    @Test
    void testCreateCharacter() {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        Mockito.when(characterRepository.save(persistenceDTO)).thenReturn(persistenceDTO);

        PlayerCharacterController controller = new PlayerCharacterController(characterRepository, adapter, itemAdapter);

        // though I would like to go through the mockMvc, my mocks won't hold, since a different object is created at
        // runtime out of the string you throw in via the REST call
        ResponseEntity<PlayerCharacterDTO> actual = controller.createCharacter(playerCharacterDTO);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(playerCharacterDTO, actual.getBody());

        verify(adapter).fromPlayerCharacterDTO(playerCharacterDTO);
        verify(characterRepository).save(persistenceDTO);
        verify(adapter).toPlayerCharacterDTO(persistenceDTO);
        verifyNoMoreInteractions(adapter, characterRepository);
    }

    @Test
    void testGetCharacterInventory() throws Exception {
        // Given
        String characterName = "Gandalf";
        ItemPersistenceDTO staffPersistence = new ItemPersistenceDTO(1L, "Staff of Power", "A mighty staff", 2.0);
        ItemPersistenceDTO pipePersistence = new ItemPersistenceDTO(2L, "Longbottom Leaf Pipe", "A fine pipe", 0.2);
        PlayerCharacterPersistenceDTO gandalfPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, 10,10,10,18,18,14);
        gandalfPersistence.getInventory().add(staffPersistence);
        gandalfPersistence.getInventory().add(pipePersistence);

        ItemDTO staffDTO = new ItemDTO("Staff of Power", "A mighty staff", 2.0);
        ItemDTO pipeDTO = new ItemDTO("Longbottom Leaf Pipe", "A fine pipe", 0.2);

        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(gandalfPersistence));
        // Mock the injected itemAdapter directly
        when(itemAdapter.toItemDTO(staffPersistence)).thenReturn(staffDTO);
        when(itemAdapter.toItemDTO(pipePersistence)).thenReturn(pipeDTO);

        // When & Then
        mockMvc.perform(get("/characters/{name}/items", characterName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Staff of Power"))
                .andExpect(jsonPath("$[0].description").value("A mighty staff"))
                .andExpect(jsonPath("$[0].weight").value(2.0))
                .andExpect(jsonPath("$[1].name").value("Longbottom Leaf Pipe"))
                .andExpect(jsonPath("$[1].description").value("A fine pipe"))
                .andExpect(jsonPath("$[1].weight").value(0.2));

        verify(characterRepository).findByName(characterName);
        // Verify interaction with the directly mocked itemAdapter
        verify(itemAdapter).toItemDTO(staffPersistence);
        verify(itemAdapter).toItemDTO(pipePersistence);
    }

    @Test
    void testGetCharacterInventory_characterNotFound() throws Exception {
        // Given
        String characterName = "Bilbo";
        when(characterRepository.findByName(characterName)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/characters/{name}/items", characterName))
                .andExpect(status().isNotFound());

        verify(characterRepository).findByName(characterName);
    }
}
