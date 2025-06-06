package com.example.dnd_backend.controllers;

import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/characters")
public class CharacterController {
    private final CharacterEventProducer eventProducer;

    public CharacterController(CharacterEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @GetMapping
    public ResponseEntity<List<PlayerCharacterDTO>> getCharacters() {
        // TODO: Implement query from event store
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacterDTO> getCharacter(@PathVariable String name) {
        // TODO: Implement query from event store
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PlayerCharacterDTO> createCharacter(@RequestBody PlayerCharacterDTO characterDTO) {
        CharacterCreated event = new CharacterCreated(characterDTO);
        eventProducer.sendEvent(event);
        return ResponseEntity.ok(characterDTO);
    }
}
