package com.example.dnd_backend.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventHandler {

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
        // TODO: Implement character creation logic
    }

    private void handleCharacterUpdated(CharacterUpdated event) {
        // TODO: Implement character update logic
    }

    private void handleItemAdded(ItemAdded event) {
        // TODO: Implement item addition logic
    }

    private void handleItemRemoved(ItemRemoved event) {
        // TODO: Implement item removal logic
    }
}
