package com.example.dnd_backend.controllers;

import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterEventProducer;
import com.example.dnd_backend.persistence.CharacterDTOAdapter;
import com.example.dnd_backend.persistence.CharacterStats;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
class CharacterControllerTests {
    @MockitoBean
    private CharacterEventProducer eventProducer;

    @MockitoBean
    private CharacterDTOAdapter dtoAdapter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacters() throws Exception {
        // TODO: Implement test for event-sourced getCharacters
        mockMvc.perform(get("/characters"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCharacter() throws Exception {
        // TODO: Implement test for event-sourced getCharacter
        mockMvc.perform(get("/characters/Bob"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCharacter() {
        CharacterStats bobStats = new CharacterStats(18, 8, 16, 12, 8, 19);
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", bobStats);
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", bobStats, new HashSet<>());

        when(dtoAdapter.toPersistenceDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(dtoAdapter.fromPersistenceDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        CharacterController controller = new CharacterController(eventProducer, dtoAdapter);

        ResponseEntity<PlayerCharacterDTO> actual = controller.createCharacter(playerCharacterDTO);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(playerCharacterDTO, actual.getBody());

        verify(eventProducer).sendEvent(any(CharacterCreated.class));
        verify(dtoAdapter).toPersistenceDTO(playerCharacterDTO);
        verify(dtoAdapter).fromPersistenceDTO(persistenceDTO);
    }
}
