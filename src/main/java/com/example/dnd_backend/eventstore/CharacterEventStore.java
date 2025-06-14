package com.example.dnd_backend.eventstore;

import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.events.CharacterEvent;
import com.example.dnd_backend.events.CharacterCreated;
import com.example.dnd_backend.events.CharacterUpdated;
import com.example.dnd_backend.events.ItemAdded;
import com.example.dnd_backend.events.ItemRemoved;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CharacterEventStore {
    private final Map<String, List<CharacterEvent>> eventStore = new HashMap<>();

    public void addEvent(CharacterEvent event) {
        String characterName = event.getCharacterName();
        eventStore.computeIfAbsent(characterName, k -> new ArrayList<>())
                .add(event);
    }

    public Optional<PlayerCharacterPersistenceDTO> getCharacter(String name) {
        List<CharacterEvent> events = eventStore.get(name);
        if (events == null) {
            return Optional.empty();
        }
        return Optional.of(replayEvents(events));
    }

    public List<PlayerCharacterPersistenceDTO> getAllCharacters() {
        return eventStore.values().stream()
            .map(events -> replayEvents(events))
            .collect(Collectors.toList());
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
        addEvent(new ItemAdded(characterName, item.getName(), item.getDescription(), item.getWeight()));
    }

    public void removeItem(String characterName, ItemPersistenceDTO item) {
        addEvent(new ItemRemoved(characterName, item.getName()));
    }
}
