package com.example.dnd_backend.gateway.eventstore;

import com.example.dnd_backend.application.CharacterProjector;
import com.example.dnd_backend.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.example.dnd_backend.gateway.eventstore.CharacterEventStore.CHARACTER_EVENTS_TOPIC;

@RequiredArgsConstructor
@Component
public class EventListener {
    private final CharacterProjector projector;
    private final Logger logger = LoggerFactory.getLogger(EventListener.class);

    @KafkaListener(
            topics = CHARACTER_EVENTS_TOPIC,
            groupId = "dnd-backend-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onEvent(DomainEvent event) {
        String message = String.format("processing this event: %s %s", event.getName(), event.getType());
        logger.info(message);
        projector.processEvent(event);
    }
}
