package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
public class CharacterManager implements CharacterProjector {
    List<PlayerCharacter> characters;
    private final CharacterRepository repository;

    public CharacterManager(CharacterRepository repository) {
        this.repository = repository;
        this.characters = repository.getAllCharacters();
    }

    public Optional<PlayerCharacter> getCharacterByName(String name) {
        return characters.stream()
                .filter(character -> character.getName().equals(name))
                .findFirst();
    }

    public boolean characterExists(String name) {
        return characters.stream()
                .anyMatch(character -> character.getName().equals(name));
    }
}
