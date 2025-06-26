package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.*;
import com.example.dnd_backend.domain.value_objects.CharacterStats;
import com.example.dnd_backend.domain.value_objects.Item;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CharacterManagerTest {
    CharacterManager characterManager = new CharacterManager();
    PlayerCharacter character = new PlayerCharacter("Alice", new CharacterStats());
    Item item = new Item("Staff", "so cool", 8);

    @Test
    void testProcessEvent_CharacterCreated() {
        // given a CharacterCreated event
        DomainEvent event = new CharacterCreated(character);
        // when the event is processed
        characterManager.processEvent(event);
        // then the manager contains the character
        assertTrue(characterManager.exists(character.getName()));
    }

    @Test
    void testProcessEvent_CharacterUpdated() {
        // given a character exists and a CharacterUpdated event is sent
        characterManager.processEvent(new CharacterCreated(character));
        PlayerCharacter updatedCharacter = new PlayerCharacter(character.getName(), new CharacterStats(character.getStats().strength() + 1, 12, 10, 13, 8, 19));
        DomainEvent event = new CharacterUpdated(updatedCharacter);
        // when the event is processed
        characterManager.processEvent(event);
        // then the manager contains the new character and not the old one
        Optional<PlayerCharacter> storedCharacter = characterManager.getByName(character.getName());
        assertTrue(storedCharacter.isPresent());
        assertEquals(storedCharacter.get().getStats().strength(), updatedCharacter.getStats().strength());
    }

    @Test
    void testProcessEvent_ItemAdded_doesNotExist() {
        // given a character exists and they don't have the item yet and an ItemAdded event is sent
        characterManager.processEvent(new CharacterCreated(character));
        DomainEvent event = new ItemAdded(character.getName(), item, 1);
        // when the event is processed
        characterManager.processEvent(event);
        // then the character contains the item once
        Map<String, Integer> inventory = character.getInventory().getCountPerItem();
        assertEquals(1, (int) inventory.get(item.name()));
    }

    @Test
    void testProcessEvent_ItemAdded_alreadyExists() {
        // given a character exists and they already have the item and an ItemAdded event is sent
        characterManager.processEvent(new CharacterCreated(character));
        characterManager.processEvent(new ItemAdded(character.getName(), item, 1));
        DomainEvent event = new ItemAdded(character.getName(), item, 1);
        // when the event is processed
        characterManager.processEvent(event);
        // then the character contains the item twice
        Map<String, Integer> inventory = character.getInventory().getCountPerItem();
        assertEquals(2, (int) inventory.get(item.name()));
    }

    @Test
    void testProcessEvent_ItemRemoved_existsTwice() {
        // given a character exists and they have the item twice and an ItemRemoved event is sent
        characterManager.processEvent(new CharacterCreated(character));
        characterManager.processEvent(new ItemAdded(character.getName(), item, 2));
        DomainEvent event = new ItemRemoved(character.getName(), item, 1);
        // when the event is processed
        characterManager.processEvent(event);
        // then the character contains the item once
        Map<String, Integer> inventory = character.getInventory().getCountPerItem();
        assertEquals(1, (int) inventory.get(item.name()));
    }

    @Test
    void testProcessEvent_ItemRemoved_existsOnce() {
        // given a character exists and they have the item once and an ItemRemoved event is sent
        characterManager.processEvent(new CharacterCreated(character));
        characterManager.processEvent(new ItemAdded(character.getName(), item, 1));
        DomainEvent event = new ItemRemoved(character.getName(), item, 1);
        // when the event is processed
        characterManager.processEvent(event);
        // then the character contains the item once
        Map<String, Integer> inventory = character.getInventory().getCountPerItem();
        assertFalse(inventory.containsKey(item.name()));
    }
}