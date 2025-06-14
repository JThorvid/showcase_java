package com.example.dnd_backend.eventstore;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.events.CharacterEvent;
import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterUpdated;
import com.example.dnd_backend.events.ItemAdded;
import com.example.dnd_backend.events.ItemRemoved;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Component
@RequiredArgsConstructor
public class CharacterEventStore {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    private final String CHARACTER_EVENTS_TOPIC = "character-events";

    private Consumer<String, CharacterEvent> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "character-event-store-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CharacterEventDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());
        
        return new KafkaConsumer<>(props);
    }

    public Optional<PlayerCharacterPersistenceDTO> getCharacter(String name) {
        try (Consumer<String, CharacterEvent> consumer = createConsumer()) {
            consumer.subscribe(List.of(CHARACTER_EVENTS_TOPIC));
            List<CharacterEvent> events = new ArrayList<>();
            
            // Poll until we get all events for this character
            while (true) {
                ConsumerRecords<String, CharacterEvent> records = consumer.poll(Duration.ofMillis(100));
                boolean foundEvents = false;
                
                for (ConsumerRecord<String, CharacterEvent> record : records) {
                    if (record.key().equals(name)) {
                        events.add(record.value());
                        foundEvents = true;
                    }
                }
                
                if (!foundEvents) {
                    break;
                }
            }
            
            if (events.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.of(replayEvents(events));
        }
    }

    public List<PlayerCharacterPersistenceDTO> getAllCharacters() {
        try (Consumer<String, CharacterEvent> consumer = createConsumer()) {
            consumer.subscribe(List.of(CHARACTER_EVENTS_TOPIC));
            List<PlayerCharacterPersistenceDTO> characters = new ArrayList<>();
            List<CharacterEvent> currentEvents = new ArrayList<>();
            String currentCharacter = null;
            
            while (true) {
                ConsumerRecords<String, CharacterEvent> records = consumer.poll(Duration.ofMillis(100));
                boolean foundEvents = false;
                
                for (ConsumerRecord<String, CharacterEvent> record : records) {
                    String characterName = record.key();
                    CharacterEvent event = record.value();
                    
                    if (currentCharacter == null || !currentCharacter.equals(characterName)) {
                        if (!currentEvents.isEmpty()) {
                            characters.add(replayEvents(currentEvents));
                            currentEvents.clear();
                        }
                        currentCharacter = characterName;
                    }
                    
                    currentEvents.add(event);
                    foundEvents = true;
                }
                
                if (!foundEvents) {
                    break;
                }
            }
            
            if (!currentEvents.isEmpty()) {
                characters.add(replayEvents(currentEvents));
            }
            
            return characters;
        }
    }

    private PlayerCharacterPersistenceDTO replayEvents(List<CharacterEvent> events) {
        PlayerCharacterPersistenceDTO character = null;
        for (CharacterEvent event : events) {
            if (event instanceof CharacterCreated created) {
                character = created.getCharacter();
            } else if (character != null) {
                if (event instanceof CharacterUpdated updated) {
                    character.setStats(updated.getStats());
                } else if (event instanceof ItemAdded added) {
                    ItemPersistenceDTO item = new ItemPersistenceDTO();
                    item.setName(added.getItemName());
                    item.setDescription(added.getDescription());
                    item.setWeight(added.getWeight());
                    character.addItem(item);
                } else if (event instanceof ItemRemoved removed) {
                    ItemPersistenceDTO item = new ItemPersistenceDTO();
                    item.setName(removed.getItemName());
                    character.removeItem(item);
                }
            }
        }
        return character;
    }

    public void addItem(String characterName, ItemPersistenceDTO item) {
        throw new UnsupportedOperationException("Event store is read-only. Use event producer to add items.");
    }

    public void removeItem(String characterName, ItemPersistenceDTO item) {
        throw new UnsupportedOperationException("Event store is read-only. Use event producer to remove items.");
    }
}
