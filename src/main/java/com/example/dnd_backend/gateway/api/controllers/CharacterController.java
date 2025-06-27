package com.example.dnd_backend.gateway.api.controllers;

import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.errors.CharacterAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.services.CharacterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
@AllArgsConstructor
public class CharacterController {
    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<List<CharacterDTO>> getCharacters() {
        return ResponseEntity.ok(characterService.getCharacters());
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<Object> getCharacter(@PathVariable String name) {
        try {
            return ResponseEntity.ok(characterService.getCharacter(name));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCharacter(@RequestBody CharacterDTO characterDTO) {
        try {
            return ResponseEntity.ok(characterService.createCharacter(characterDTO));
        } catch (CharacterAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateCharacter(@RequestBody CharacterDTO characterDTO) {
        try {
            return ResponseEntity.ok(characterService.updateCharacter(characterDTO));
        } catch (CharacterNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
