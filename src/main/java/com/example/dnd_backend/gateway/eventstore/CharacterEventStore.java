package com.example.dnd_backend.gateway.eventstore;

import com.example.dnd_backend.application.EventRepository;
import com.example.dnd_backend.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventStore implements EventRepository {
    public static final String CHARACTER_EVENTS_TOPIC = "character-events";

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void sendEvent(DomainEvent event) {
        kafkaTemplate.send(CHARACTER_EVENTS_TOPIC, event.getName(), event);
    }
}
