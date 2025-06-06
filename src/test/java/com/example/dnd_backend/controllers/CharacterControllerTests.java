package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
class CharacterControllerTests {
    @MockitoBean
    private CharacterRepository characterRepository;

    @MockitoBean
    private CharacterDTOAdapter adapter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacters() throws Exception {
        CharacterStatsDTO bobStatsDTO = new CharacterStatsDTO(18, 8, 16, 12, 8, 19);
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", bobStatsDTO);

        CharacterStats bobStatsPersistence = new CharacterStats(18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", bobStatsPersistence, new HashSet<>());

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
        CharacterStatsDTO bobStatsDTO = new CharacterStatsDTO(18, 8, 16, 12, 8, 19);
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", bobStatsDTO);

        CharacterStats bobStatsPersistence = new CharacterStats(18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", bobStatsPersistence, new HashSet<>());

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
        CharacterStatsDTO bobStatsDTO = new CharacterStatsDTO(18, 8, 16, 12, 8, 19);
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", bobStatsDTO);

        CharacterStats bobStatsPersistence = new CharacterStats(18, 8, 16, 12, 8, 19);
        PlayerCharacterPersistenceDTO persistenceToSave = new PlayerCharacterPersistenceDTO(null, "Bob", bobStatsPersistence, new HashSet<>());
        PlayerCharacterPersistenceDTO persistenceSaved = new PlayerCharacterPersistenceDTO(1L, "Bob", bobStatsPersistence, new HashSet<>());

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceToSave);
        when(characterRepository.save(persistenceToSave)).thenReturn(persistenceSaved);
        when(adapter.toPlayerCharacterDTO(persistenceSaved)).thenReturn(playerCharacterDTO);

        CharacterController controller = new CharacterController(characterRepository, adapter);

        ResponseEntity<PlayerCharacterDTO> actual = controller.createCharacter(playerCharacterDTO);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(playerCharacterDTO, actual.getBody());

        verify(adapter).fromPlayerCharacterDTO(playerCharacterDTO);
        verify(characterRepository).save(persistenceToSave);
        verify(adapter).toPlayerCharacterDTO(persistenceSaved);
        verifyNoMoreInteractions(characterRepository);
    }
}
