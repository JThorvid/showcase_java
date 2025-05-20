package com.example.dnd_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CharacterController {
    CharacterDTO dummy = new CharacterDTO(
            "Bob", 18, 8, 16, 12, 8, 19
    );

    @GetMapping("/characters")
    public List<CharacterDTO> getCharacters() {
        List<CharacterDTO> characters = new ArrayList<>();
        characters.add(dummy);
        return characters;
    }

    @GetMapping("/characters/{name}")
    public CharacterDTO getCharacter(@PathVariable String name) {
        return dummy;
    }
}
