package com.example.dnd_backend.events;

import com.example.dnd_backend.persistence.CharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterEventProducer {
    private final KafkaTemplate<String, CharacterEvent> kafkaTemplate;
    private final CharacterDTOAdapter dtoAdapter;

    public void sendEvent(CharacterEvent event) {
        kafkaTemplate.send("character-events", event.getCharacterName(), event);
    }
}
