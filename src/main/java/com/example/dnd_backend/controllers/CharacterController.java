package com.example.dnd_backend.controllers;

import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.DomainEvent;
import com.example.dnd_backend.eventstore.CharacterEventStore;
import com.example.dnd_backend.persistence.CharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
public class CharacterController {
    private final CharacterEventStore eventStore;
    private final CharacterDTOAdapter dtoAdapter;

    public CharacterController(CharacterEventStore eventStore, CharacterDTOAdapter dtoAdapter) {
        this.eventStore = eventStore;
        this.dtoAdapter = dtoAdapter;
    }

    @GetMapping
    public ResponseEntity<List<PlayerCharacter>> getCharacters() {

        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacter> getCharacter(@PathVariable String name) {
        List<DomainEvent> events = eventStore.getEvents(name);

        return ResponseEntity.ok(PlayerCharacter.rehydrate(events));
    }

    @PostMapping
    public ResponseEntity<PlayerCharacter> createCharacter(@RequestBody PlayerCharacter characterDTO) {
        PlayerCharacterPersistenceDTO persistenceDTO = dtoAdapter.toPersistenceDTO(characterDTO);
        CharacterCreated event = new CharacterCreated(persistenceDTO);
        eventStore.sendEvent(event);
        return ResponseEntity.ok(dtoAdapter.fromPersistenceDTO(persistenceDTO));
    }
}
