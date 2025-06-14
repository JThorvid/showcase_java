package com.example.dnd_backend.eventstore;

import com.example.dnd_backend.events.CharacterEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

public class CharacterEventDeserializer implements Deserializer<CharacterEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public CharacterEvent deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, CharacterEvent.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing CharacterEvent", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
