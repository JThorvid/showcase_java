package com.example.dnd_backend.controllers;

import com.example.dnd_backend.entities.PlayerCharacter;
import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.DomainEvent;
import com.example.dnd_backend.eventstore.CharacterEventStore;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/characters")
@AllArgsConstructor
public class CharacterController {
    private final CharacterEventStore eventStore;

    @GetMapping
    public ResponseEntity<List<PlayerCharacter>> getCharacters() {
        List<String> characterNames = eventStore.getKeys();
        List<PlayerCharacter> characters = new ArrayList<>();
        for (String characterName : characterNames) {
            List<DomainEvent> events = eventStore.getEvents(characterName);
            characters.add(PlayerCharacter.rehydrate(events));
        }
        return ResponseEntity.ok(characters);
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacter> getCharacter(@PathVariable String name) {
        List<DomainEvent> events = eventStore.getEvents(name);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PlayerCharacter.rehydrate(events));
    }

    @PostMapping
    public ResponseEntity<PlayerCharacter> createCharacter(@RequestBody PlayerCharacter characterDTO) {
        CharacterCreated event = new CharacterCreated(characterDTO);
        eventStore.sendEvent(event);
        return ResponseEntity.ok(characterDTO);
    }
}
