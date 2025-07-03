package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.CharacterManager;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.dtos.CharacterDtoAdapter;
import com.example.dnd_backend.gateway.api.errors.CharacterAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.eventstore.CharacterEventStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ActiveProfiles("test")
@SpringBootTest
class CharacterServiceTests {
    @MockitoBean
    private CharacterEventStore eventStore;

    @MockitoBean
    private CharacterManager characterManager;

    private final PlayerCharacter alice = new PlayerCharacter("Alice", new CharacterStats());
    private final CharacterDTO aliceDTO = CharacterDtoAdapter.characterToDto(alice);
    private final PlayerCharacter bob = new PlayerCharacter("Bob", new CharacterStats());
    private final CharacterDTO bobDTO = CharacterDtoAdapter.characterToDto(bob);

    @Autowired
    private CharacterService service;

    @Test
    void testGetCharacters_emptyList() {
        // given no characters exist yet
        Mockito.when(characterManager.getAll()).thenReturn(List.of());
        // when all characters are requested
        List<CharacterDTO> characters = service.getCharacters();
        // then an empty list is returned
        assertEquals(0, characters.size());
    }

    @Test
    void testGetCharacters_nonEmptyList() {
        // given two characters exist
        Mockito.when(characterManager.getAll()).thenReturn(List.of(alice, bob));
        // when all characters are requested
        List<CharacterDTO> characters = service.getCharacters();
        // then both characters are returned
        assertEquals(2, characters.size());
        assertTrue(characters.contains(aliceDTO));
        assertTrue(characters.contains(bobDTO));
    }

    @Test
    void testGetCharacter_nonExistentCharacter() {
        // given no characters exist
        Mockito.when(characterManager.getByName(any())).thenReturn(Optional.empty());
        // when a specific character is requested
        // then an exception is thrown
        assertThrows(CharacterNotFoundException.class, () -> service.getCharacter("Alice"));
    }

    @Test
    void testGetCharacter_existentCharacter() {
        // given Alice exists
        Mockito.when(characterManager.getByName(alice.getName())).thenReturn(Optional.of(alice));
        // when Alice is requested
        CharacterDTO characterDTO = service.getCharacter(alice.getName());
        // then Alice gets returned
        assertEquals(aliceDTO, characterDTO);
    }

    @Test
    void testCreateCharacter_nonExistentCharacter() {
        // given Bob does not yet exist
        Mockito.when(characterManager.exists(bob.getName())).thenReturn(false);
        // when Bob is created
        CharacterDTO actualDTO = service.createCharacter(bobDTO);
        // then Bob is returned
        assertEquals(bobDTO, actualDTO);
        // and a CharacterCreated event gets sent
        verify(eventStore).sendEvent(any(CharacterCreated.class));
    }

    @Test
    void testCreateCharacter_existentCharacter() {
        // given Bob exists
        Mockito.when(characterManager.exists(bob.getName())).thenReturn(true);
        // when Bob gets created again
        // then an exception is thrown
        assertThrows(CharacterAlreadyExistsException.class, () -> service.createCharacter(bobDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateCharacter_nonExistentCharacter() {
        // given Alice does not exist yet
        Mockito.when(characterManager.exists(alice.getName())).thenReturn(false);
        // when Alice gets updated
        // then an exception is thrown
        assertThrows(CharacterNotFoundException.class, () -> service.updateCharacter(aliceDTO));
        // and no events are sent
        verifyNoInteractions(eventStore);
    }

    @Test
    void testUpdateCharacter_existentCharacter() {
        // given Alice exists
        Mockito.when(characterManager.exists(alice.getName())).thenReturn(true);
        // when Alice gets updated
        PlayerCharacter newAlice = new PlayerCharacter("Alice", new CharacterStats(19, 8, 16, 18, 12, 10));
        CharacterDTO newAliceDTO = service.updateCharacter(CharacterDtoAdapter.characterToDto(newAlice));
        // then the new Alice gets returned
        assertEquals(newAlice, newAliceDTO.toEntity());
        // and a CharacterUpdated event gets sent
        verify(eventStore).sendEvent(any(CharacterUpdated.class));
    }
}
