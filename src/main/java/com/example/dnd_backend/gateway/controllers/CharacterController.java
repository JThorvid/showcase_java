package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.gateway.eventstore.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/characters")
@AllArgsConstructor
public class CharacterController {
    private final EventRepository eventStore;
    private final Projector<PlayerCharacter> projector;

    @GetMapping
    public ResponseEntity<List<CharacterDTO>> getCharacters() {
        return ResponseEntity.ok(projector.getAll().stream().map(CharacterDTO::new).toList());
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<CharacterDTO> getCharacter(@PathVariable String name) {
        Optional<PlayerCharacter> character = projector.getByName(name);
        if (character.isPresent()) {
            CharacterDTO characterDTO = new CharacterDTO(character.get());
            return ResponseEntity.ok(characterDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCharacter(@RequestBody CharacterDTO characterDTO) {
        PlayerCharacter character = characterDTO.toEntity();
        boolean exists = projector.exists(character.getName());
        if (exists) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", String.format("A character named %s already exists", character.getName())));
        }

        CharacterCreated event = new CharacterCreated(character);
        eventStore.sendEvent(event);
        return ResponseEntity.ok(characterDTO);
    }

    @PutMapping
    public ResponseEntity<Object> updateCharacter(@RequestBody CharacterDTO characterDTO) {
        PlayerCharacter character = characterDTO.toEntity();
        boolean exists = projector.exists(character.getName());
        if (exists) {
            CharacterUpdated event = new CharacterUpdated(character);
            eventStore.sendEvent(event);
            return ResponseEntity.ok(characterDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
