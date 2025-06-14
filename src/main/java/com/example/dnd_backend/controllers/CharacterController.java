package com.example.dnd_backend.controllers;

import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterEventProducer;
import com.example.dnd_backend.persistence.CharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/characters")
public class CharacterController {
    private final CharacterEventProducer eventProducer;
    private final CharacterDTOAdapter dtoAdapter;

    public CharacterController(CharacterEventProducer eventProducer, CharacterDTOAdapter dtoAdapter) {
        this.eventProducer = eventProducer;
        this.dtoAdapter = dtoAdapter;
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
        PlayerCharacterPersistenceDTO persistenceDTO = dtoAdapter.toPersistenceDTO(characterDTO);
        CharacterCreated event = new CharacterCreated(persistenceDTO);
        eventProducer.sendEvent(event);
        return ResponseEntity.ok(dtoAdapter.fromPersistenceDTO(persistenceDTO));
    }
}
