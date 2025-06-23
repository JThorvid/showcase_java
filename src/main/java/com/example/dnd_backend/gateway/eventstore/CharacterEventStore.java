package com.example.dnd_backend.gateway.eventstore;

import com.example.dnd_backend.entities.PlayerCharacter;
import com.example.dnd_backend.gateway.events.CharacterCreated;
import com.example.dnd_backend.gateway.events.CharacterUpdated;
import com.example.dnd_backend.gateway.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CharacterEventStore {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private static final String CHARACTER_EVENTS_TOPIC = "character-events";

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final Map<String, List<DomainEvent>> events = new ConcurrentHashMap<>();

    public void sendEvent(DomainEvent event) {
        kafkaTemplate.send(CHARACTER_EVENTS_TOPIC, event.getName(), event);
    }

    @KafkaListener(topics = CHARACTER_EVENTS_TOPIC, groupId = "dnd-backend-consumer")
    public void onEvent(DomainEvent event) {
        events.computeIfAbsent(event.getName(), k -> new ArrayList<>()).add(event);
    }

    public List<String> getKeys() {
        return events.keySet().stream().toList();
    }

    public List<DomainEvent> getEvents(String name) {
        return events.getOrDefault(name, List.of());
    }

    private Consumer<String, DomainEvent> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dnd-backend-consumer-manual");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.dnd_backend.*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.dnd_backend.gateway.events.DomainEvent");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); // ignore the embedded __TypeId__ header
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new KafkaConsumer<>(props);
    }

    public Optional<PlayerCharacter> getCharacter(String name) {
        try (Consumer<String, DomainEvent> consumer = createConsumer()) {
            consumer.subscribe(List.of(CHARACTER_EVENTS_TOPIC));
            Set<TopicPartition> setOfPartitions = consumer.assignment();
            while (setOfPartitions.isEmpty()) {
                consumer.poll(Duration.ofMillis(100));
                setOfPartitions = consumer.assignment();
            }
            consumer.seekToBeginning(setOfPartitions);
            List<DomainEvent> events = new ArrayList<>();

            // Poll until we get all events for this character
            while (true) {
                ConsumerRecords<String, DomainEvent> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    break;
                } else {
                    for (ConsumerRecord<String, DomainEvent> record : records) {
                        if (record.key().equals(name)) {
                            events.add(record.value());
                        }
                    }
                }
            }

            if (events.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(replayEvents(events));
        }
    }

    //
//    public List<PlayerCharacterPersistenceDTO> getAllCharacters() {
//        try (Consumer<String, CharacterEvent> consumer = createConsumer()) {
//            consumer.subscribe(List.of(CHARACTER_EVENTS_TOPIC));
//            List<PlayerCharacterPersistenceDTO> characters = new ArrayList<>();
//            List<CharacterEvent> events = new ArrayList<>();
//            String currentCharacter = null;
//
//            while (true) {
//                ConsumerRecords<String, CharacterEvent> records = consumer.poll(Duration.ofMillis(100));
//
//                if (records.isEmpty()) {
//                    break;
//                } else {
//                    for (ConsumerRecord<String, CharacterEvent> record : records) {
//                        String characterName = record.key();
//                        CharacterEvent event = record.value();
//
//                        if (currentCharacter == null || !currentCharacter.equals(characterName)) {
//                            if (!events.isEmpty()) {
//                                characters.add(replayEvents(events));
//                                events.clear();
//                            }
//                            currentCharacter = characterName;
//                        }
//
//                        events.add(event);
//                    }
//                }
//            }
//
//            if (!events.isEmpty()) {
//                characters.add(replayEvents(events));
//            }
//
//            return characters;
//        }
//    }
//
    private PlayerCharacter replayEvents(List<DomainEvent> events) {
        PlayerCharacter character = null;
        for (DomainEvent event : events) {
            if (event instanceof CharacterCreated created) {
                character = created.getCharacter();
            } else if (character != null) {
                if (event instanceof CharacterUpdated updated) {
                    character.setStats(updated.getStats());
//                } else if (event instanceof ItemAdded added) {
//                    ItemPersistenceDTO item = new ItemPersistenceDTO();
//                    item.setName(added.getItemName());
//                    item.setDescription(added.getDescription());
//                    item.setWeight(added.getWeight());
//                    character.addItem(item);
//                } else if (event instanceof ItemRemoved removed) {
//                    ItemPersistenceDTO item = new ItemPersistenceDTO();
//                    item.setName(removed.getItemName());
//                    character.removeItem(item);
                }
            }
        }
        return character;
    }
//
//    public void addItem(String characterName, ItemPersistenceDTO item) {
//        throw new UnsupportedOperationException("Event store is read-only. Use event producer to add items.");
//    }
//
//    public void removeItem(String characterName, ItemPersistenceDTO item) {
//        throw new UnsupportedOperationException("Event store is read-only. Use event producer to remove items.");
//    }
}
