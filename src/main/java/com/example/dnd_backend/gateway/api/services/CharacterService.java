package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.dtos.CharacterDtoAdapter;
import com.example.dnd_backend.gateway.api.errors.CharacterAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.eventstore.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final EventRepository eventStore;
    private final Projector<PlayerCharacter> projector;

    public List<CharacterDTO> getCharacters() {
        return projector.getAll().stream().map(CharacterDtoAdapter::characterToDto).toList();
    }

    public CharacterDTO getCharacter(String name) throws CharacterNotFoundException {
        return projector.getByName(name).map(CharacterDtoAdapter::characterToDto)
                .orElseThrow(() -> new CharacterNotFoundException(name));
    }

    public CharacterDTO createCharacter(CharacterDTO characterDTO) throws CharacterAlreadyExistsException {
        PlayerCharacter character = characterDTO.toEntity();
        if (projector.exists(character.getName())) {
            throw new CharacterAlreadyExistsException(character.getName());
        }
        CharacterCreated event = new CharacterCreated(character);
        eventStore.sendEvent(event);
        return characterDTO;
    }

    public CharacterDTO updateCharacter(CharacterDTO characterDTO) throws CharacterNotFoundException {
        PlayerCharacter character = characterDTO.toEntity();
        if (!projector.exists(character.getName())) {
            throw new CharacterNotFoundException(character.getName());
        }
        CharacterUpdated event = new CharacterUpdated(character);
        eventStore.sendEvent(event);
        return characterDTO;
    }
} 