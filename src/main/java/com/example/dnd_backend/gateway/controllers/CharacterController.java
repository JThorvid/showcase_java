package com.example.dnd_backend.gateway.controllers;

import com.example.dnd_backend.application.CharacterProjector;
import com.example.dnd_backend.application.CharacterRepository;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
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
    private final CharacterRepository eventStore;
    private final CharacterProjector characterProjector;

    @GetMapping
    public ResponseEntity<List<PlayerCharacter>> getCharacters() {
//        List<String> characterNames = eventStore.getKeys();
//        List<PlayerCharacter> characters = new ArrayList<>();
//        for (String characterName : characterNames) {
//            List<DomainEvent> events = eventStore.getEvents(characterName);
//            characters.add(PlayerCharacter.rehydrate(events));
//        }
//        return ResponseEntity.ok(characters);
        return ResponseEntity.ok(characterProjector.getCharacters());
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacter> getCharacter(@PathVariable String name) {
        Optional<PlayerCharacter> character = characterProjector.getCharacterByName(name);
        return character.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//        Optional<PlayerCharacter> pc = eventStore.getCharacter(name);
//        return pc.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//
//        List<DomainEvent> events = eventStore.getEvents(name);
//        if (events.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(PlayerCharacter.rehydrate(events));
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
}
