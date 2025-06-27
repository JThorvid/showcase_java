package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.errors.CharacterAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.services.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
@AutoConfigureMockMvc
class CharacterControllerTests {
    @MockitoBean
    private CharacterService service;

    private final CharacterDTO aliceDTO = new CharacterDTO("Alice", 10, 10, 10, 10, 10, 10);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCharacters_emptyList() throws Exception {
        // given no characters exist
        Mockito.when(service.getCharacters()).thenReturn(List.of());
        // when all characters are requested
        mockMvc.perform(get("/characters"))
                // then an empty list is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").isEmpty());
    }

    @Test
    void testGetCharacters_nonEmptyList() throws Exception {
        // given Alice exists
        Mockito.when(service.getCharacters()).thenReturn(List.of(aliceDTO));
        // when all characters are requested
        mockMvc.perform(get("/characters"))
                // then Alice are returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").exists())
                .andExpect(jsonPath("$[0].name").value(aliceDTO.name()));
    }

    @Test
    void testGetCharacter_nonExistentCharacter() throws Exception {
        // given no characters exist
        Mockito.when(service.getCharacter(any())).thenThrow(new CharacterNotFoundException(aliceDTO.name()));
        // when one character is requested
        mockMvc.perform(get("/characters/{name}", "Claude"))
                // then a "not found" is returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCharacter_existentCharacter() throws Exception {
        // given Alice exists
        Mockito.when(service.getCharacter(aliceDTO.name())).thenReturn(aliceDTO);
        // when Alice is requested
        mockMvc.perform(get("/characters/{name}", aliceDTO.name()))
                // then Alice gets returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(aliceDTO.name()));
    }

    @Test
    void testCreateCharacter_nonExistentCharacter() throws Exception {
        // given Alice does not yet exist
        Mockito.when(service.createCharacter(aliceDTO)).thenReturn(aliceDTO);
        // when Alice is created
        mockMvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aliceDTO)))
                // then Alice gets returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(aliceDTO.name()));
    }

    @Test
    void testCreateCharacter_existentCharacter() throws Exception {
        // given Alice exists
        Mockito.when(service.createCharacter(aliceDTO)).thenThrow(new CharacterAlreadyExistsException(aliceDTO.name()));
        // when Alice gets created again
        mockMvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aliceDTO)))
                // then a "bad request" gets returned
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCharacter_nonExistentCharacter() throws Exception {
        // given Alice does not exist yet
        Mockito.when(service.updateCharacter(aliceDTO)).thenThrow(new CharacterNotFoundException(aliceDTO.name()));
        // when Alice gets updated
        mockMvc.perform(put("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aliceDTO)))
                // then a "not found" gets returned
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCharacter_existentCharacter() throws Exception {
        // given Alice exists
        Mockito.when(service.updateCharacter(aliceDTO)).thenReturn(aliceDTO);
        // when Alice gets updated
        mockMvc.perform(put("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aliceDTO)))
                // then Alice gets returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(aliceDTO.name()));
    }
}
