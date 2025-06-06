package com.example.dnd_backend.events;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.CharacterStats;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventHandler {
    private final CharacterRepository characterRepository;

    @KafkaListener(topics = "character-events", groupId = "dnd-backend-consumer")
    public void handleEvent(CharacterEvent event) {
        switch (event.getType()) {
            case "CHARACTER_CREATED":
                handleCharacterCreated((CharacterCreated) event);
                break;
            case "CHARACTER_UPDATED":
                handleCharacterUpdated((CharacterUpdated) event);
                break;
            case "ITEM_ADDED":
                handleItemAdded((ItemAdded) event);
                break;
            case "ITEM_REMOVED":
                handleItemRemoved((ItemRemoved) event);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }
    }

    private void handleCharacterCreated(CharacterCreated event) {
        characterRepository.save(event.getCharacter().toPersistenceDTO());
    }

    private void handleCharacterUpdated(CharacterUpdated event) {
        Optional<PlayerCharacterPersistenceDTO> character = characterRepository.findByName(event.getCharacterName());
        if (character.isPresent()) {
            PlayerCharacterPersistenceDTO updatedCharacter = character.get();
            updatedCharacter.setStats(event.getStats());
            characterRepository.save(updatedCharacter);
        }
        characterRepository.save(characterDTO.toPersistenceDTO());
    }

    private void handleItemAdded(ItemAdded event) {
        // TODO: Implement item addition logic
    }

    private void handleItemRemoved(ItemRemoved event) {
        // TODO: Implement item removal logic
    }
}
