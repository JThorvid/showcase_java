package com.example.dnd_backend.events;

import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.eventstore.CharacterEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventHandler {
    private final CharacterEventStore eventStore;

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
        // No need to store the event, it's already in Kafka
    }

    private void handleCharacterUpdated(CharacterUpdated event) {
        eventStore.getCharacter(event.getCharacterName())
                .ifPresent(character -> {
                    character.setStats(event.getStats());
                });
    }

    private void handleItemAdded(ItemAdded event) {
        eventStore.getCharacter(event.getCharacterName())
                .ifPresent(character -> {
                    ItemPersistenceDTO item = new ItemPersistenceDTO();
                    item.setName(event.getItemName());
                    item.setDescription(event.getDescription());
                    item.setWeight(event.getWeight());
                    character.addItem(item);
                });
    }

    private void handleItemRemoved(ItemRemoved event) {
        eventStore.getCharacter(event.getCharacterName())
                .ifPresent(character -> {
                    ItemPersistenceDTO item = new ItemPersistenceDTO();
                    item.setName(event.getItemName());
                    character.removeItem(item);
                });
    }
}
