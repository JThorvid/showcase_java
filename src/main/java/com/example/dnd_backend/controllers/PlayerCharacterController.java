package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/characters")
public class PlayerCharacterController {
    private final CharacterRepository characterRepository;
    private final PlayerCharacterDTOAdapter adapter;

    public PlayerCharacterController(CharacterRepository characterRepository, PlayerCharacterDTOAdapter adapter) {
        this.characterRepository = characterRepository;
        this.adapter = adapter;
    }

    @GetMapping
    public List<PlayerCharacterDTO> getCharacters() {
        return StreamSupport.stream(characterRepository.findAll().spliterator(), false)
                .map(adapter::toPlayerCharacterDTO).toList();
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacterDTO> getCharacter(@PathVariable String name) {
        return characterRepository.findByName(name)
                .map(adapter::toPlayerCharacterDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlayerCharacterDTO> createCharacter(@RequestBody PlayerCharacterDTO characterDTO) {
        PlayerCharacterPersistenceDTO toSave = adapter.fromPlayerCharacterDTO(characterDTO);
        PlayerCharacterPersistenceDTO saved = characterRepository.save(
                toSave
        );
        PlayerCharacterDTO character = adapter.toPlayerCharacterDTO(
                saved
        );
        return ResponseEntity.ok(character);
    }
}
