package com.example.dnd_backend.gateway.eventstore;

import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.value_objects.Item;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.dnd_backend.gateway.eventstore.CharacterEventStore.CHARACTER_EVENTS_TOPIC;

@RequiredArgsConstructor
@Component
public class EventListener {
    private final Projector<PlayerCharacter> characterProjector;
    private final Projector<Item> itemProjector;
    private final Logger logger = LoggerFactory.getLogger(EventListener.class);

    @KafkaListener(
            topics = CHARACTER_EVENTS_TOPIC,
            groupId = "character-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCharacterEvent(DomainEvent event) {
        String message = String.format("character projector processing this event: %s %s", event.name(), event.getType());
        logger.info(message);
        characterProjector.processEvent(event);
    }

    @KafkaListener(
            topics = CHARACTER_EVENTS_TOPIC,
            groupId = "item-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onItemEvent(DomainEvent event) {
        String message = String.format("item projector processing this event: %s %s", event.name(), event.getType());
        logger.info(message);
        itemProjector.processEvent(event);
    }
}
