package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.CharacterUpdated;
import com.example.dnd_backend.domain.events.DomainEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterManager implements Projector<PlayerCharacter> {
    List<PlayerCharacter> characters = new ArrayList<>();

    @Override
    public List<PlayerCharacter> getAll() {
        return characters;
    }

    @Override
    public Optional<PlayerCharacter> getByName(String name) {
        return characters.stream()
                .filter(character -> character.getName().equals(name))
                .findFirst();
    }

    @Override
    public boolean exists(String name) {
        return characters.stream()
                .anyMatch(character -> character.getName().equals(name));
    }

    @Override
    public void processEvent(DomainEvent event) {
        if (event instanceof CharacterCreated e) {
            characters.add(e.character());
        } else if (event instanceof CharacterUpdated e) {
            String characterName = e.name();
            characters.stream()
                    .filter(it -> it.getName().equals(characterName))
                    .findFirst()
                    .ifPresent(character -> character.apply(event));
        }
    }
}
