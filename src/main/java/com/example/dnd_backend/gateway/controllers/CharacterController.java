package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.CharacterProjector;
import com.example.dnd_backend.application.EventRepository;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
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
    private final CharacterProjector characterProjector;

    @GetMapping
    public ResponseEntity<List<PlayerCharacter>> getCharacters() {
        return ResponseEntity.ok(characterProjector.getCharacters());
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacter> getCharacter(@PathVariable String name) {
        Optional<PlayerCharacter> character = characterProjector.getCharacterByName(name);
        return character.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createCharacter(@RequestBody PlayerCharacter characterDTO) {
        boolean exists = characterProjector.characterExists(characterDTO.getName());
        if (exists) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", String.format("A character named %s already exists", characterDTO.getName())));
        }

        CharacterCreated event = new CharacterCreated(characterDTO);
        eventStore.sendEvent(event);
        return ResponseEntity.ok(characterDTO);
    }

    @PutMapping
    public ResponseEntity<Object> updateCharacter(@RequestBody PlayerCharacter characterDTO) {
        boolean exists = characterProjector.characterExists(characterDTO.getName());
        if (exists) {
            CharacterUpdated event = new CharacterUpdated(characterDTO);
            eventStore.sendEvent(event);
            return ResponseEntity.ok(characterDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
