package com.example.dnd_backend.controllers;

import com.example.dnd_backend.entities.PlayerCharacter;
import com.example.dnd_backend.gateway.controllers.CharacterController;
import com.example.dnd_backend.gateway.events.CharacterCreated;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import com.example.dnd_backend.entities.CharacterStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
class CharacterControllerTests {
    @MockitoBean
    private CharacterEventStore eventStore;

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
        PlayerCharacter playerCharacter = new PlayerCharacter("Bob", bobStats);

        CharacterController controller = new CharacterController(eventStore);

        ResponseEntity<PlayerCharacter> actual = controller.createCharacter(playerCharacter);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(playerCharacter, actual.getBody());

        verify(eventStore).sendEvent(any(CharacterCreated.class));
    }
}
