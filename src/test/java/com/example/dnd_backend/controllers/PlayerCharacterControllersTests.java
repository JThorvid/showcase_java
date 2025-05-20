package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
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

        PlayerCharacterController controller = new PlayerCharacterController(characterRepository, adapter);

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
}
