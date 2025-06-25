package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.CharacterCreated;
import com.example.dnd_backend.domain.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Service
@RequiredArgsConstructor
public class CharacterManager implements CharacterProjector {
    List<PlayerCharacter> characters = new ArrayList<>();
    private final EventRepository repository;

    @Override
    public Optional<PlayerCharacter> getCharacterByName(String name) {
        return characters.stream()
                .filter(character -> character.getName().equals(name))
                .findFirst();
    }

    @Override
    public boolean characterExists(String name) {
        return characters.stream()
                .anyMatch(character -> character.getName().equals(name));
    }

    @Override
    public void processEvent(DomainEvent event) {
        if (event instanceof CharacterCreated characterCreated) {
            characters.add(characterCreated.character());
        } else {
            String characterName = event.name();
            characters.stream()
                    .filter(it -> it.getName().equals(characterName))
                    .findFirst()
                    .ifPresent(character -> character.apply(event));
        }
    }
}
