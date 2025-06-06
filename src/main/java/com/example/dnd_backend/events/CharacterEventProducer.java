package com.example.dnd_backend.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventProducer {
    private final KafkaTemplate<String, CharacterEvent> kafkaTemplate;

    public void sendEvent(CharacterEvent event) {
        kafkaTemplate.send("character-events", event.getCharacterName(), event);
    }
}
