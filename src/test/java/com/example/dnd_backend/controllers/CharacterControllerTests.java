package com.example.dnd_backend.controllers;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import com.example.dnd_backend.gateway.controllers.CharacterController;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(CharacterController.class)
class CharacterControllerTests {
    @MockitoBean
    private CharacterEventStore eventStore;

    @MockitoBean
    private CharacterManager characterManager;

    private CharacterController controller;
    private PlayerCharacter alice;
    private PlayerCharacter bob;

    @BeforeEach
    void setUp() {
        controller = new CharacterController(eventStore, characterManager);
        bob = new PlayerCharacter("Bob", new CharacterStats(18, 8, 16, 12, 8, 19));
        alice = new PlayerCharacter("Alice", new CharacterStats(8, 14, 16, 18, 12, 10));
    }

    @Test
    void testGetCharacters_emptyList() {
        // given no characters exist yet
        // when all characters are requested
        ResponseEntity<List<PlayerCharacter>> actual = controller.getCharacters();
        // then an empty list is returned
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(Objects.requireNonNull(actual.getBody()).isEmpty());
    }

    @Test
    void testGetCharacters_nonEmptyList() {
        // given two characters exist
        Mockito.when(characterManager.getCharacters()).thenReturn(List.of(alice, bob));
        // when all characters are requested
        ResponseEntity<List<PlayerCharacter>> actual = controller.getCharacters();
        // then both characters are returned
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(2, Objects.requireNonNull(actual.getBody()).size());
    }

    @Test
    void testGetCharacter_nonExistentCharacter() {
        // given no characters exist
        // when one character is requested
        ResponseEntity<PlayerCharacter> actual = controller.getCharacter("Claude");
        // a "not found" gets returned
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @Test
    void testGetCharacter_existentCharacter() {
        // given Alice exists
        Mockito.when(characterManager.getCharacterByName(alice.getName())).thenReturn(Optional.of(alice));
        // when Alice is requested
        ResponseEntity<PlayerCharacter> actual = controller.getCharacter(alice.getName());
        // then Alice gets returned
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(alice, actual.getBody());
    }

    @Test
    void testCreateCharacter_nonExistentCharacter() {
        // given Bob does not yet exist
        Mockito.when(characterManager.characterExists(bob.getName())).thenReturn(false);
        // when Bob is created
        ResponseEntity<?> actual = controller.createCharacter(bob);
        // then a CharacterCreated event gets sent
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(bob, actual.getBody());
        verify(eventStore).sendEvent(any(CharacterCreated.class));
    }

    @Test
    void testCreateCharacter_existentCharacter() {
        // given Bob exists
        Mockito.when(characterManager.characterExists(bob.getName())).thenReturn(true);
        // when Bob gets created again
        ResponseEntity<?> actual = controller.createCharacter(bob);
        // then a "bad request" gets returned and no events are created
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateCharacter_nonExistentCharacter() {
        // given Alice does not exist yet
        Mockito.when(characterManager.characterExists(alice.getName())).thenReturn(false);
        // when Alice gets updated
        PlayerCharacter newAlice = new PlayerCharacter("Alice", new CharacterStats(19, 8, 16, 18, 12, 10));
        ResponseEntity<?> actual = controller.updateCharacter(newAlice);
        // then a "not found" gets returned and no events are created
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateCharacter_existentCharacter() {
        // given Alice exists
        Mockito.when(characterManager.characterExists(alice.getName())).thenReturn(true);
        // when Alice gets updated
        PlayerCharacter newAlice = new PlayerCharacter("Alice", new CharacterStats(19, 8, 16, 18, 12, 10));
        ResponseEntity<?> actual = controller.updateCharacter(newAlice);
        // then a CharacterUpdated event gets sent
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(newAlice, actual.getBody());
        verify(eventStore).sendEvent(any(CharacterUpdated.class));
    }
}
