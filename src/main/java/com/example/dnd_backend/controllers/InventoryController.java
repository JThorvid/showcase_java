package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.CharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.persistence.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/characters/{characterName}")
public class InventoryController {
    private final CharacterRepository characterRepository;
    private final CharacterDTOAdapter adapter;
    private final ItemDTOAdapter itemAdapter;
    private final ItemRepository itemRepository;

    public InventoryController(CharacterRepository characterRepository, CharacterDTOAdapter adapter, ItemDTOAdapter itemAdapter, ItemRepository itemRepository) {
        this.characterRepository = characterRepository;
        this.adapter = adapter;
        this.itemAdapter = itemAdapter;
        this.itemRepository = itemRepository;
    }

    @GetMapping(path = "/inventory")
    public ResponseEntity<List<ItemDTO>> getCharacterInventory(@PathVariable String characterName) {
        Optional<PlayerCharacterPersistenceDTO> characterPersistenceDTO = characterRepository.findByName(characterName);
        if (characterPersistenceDTO.isPresent()) {
            Set<ItemPersistenceDTO> inventory = characterPersistenceDTO.get().getInventory();
            List<ItemDTO> itemDTOs = inventory.stream()
                    .map(itemAdapter::toItemDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(itemDTOs);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/inventory/{itemName}")
    public ResponseEntity<PlayerCharacterDTO> addItemToInventory(@PathVariable String characterName, @PathVariable String itemName) {
        Optional<PlayerCharacterPersistenceDTO> characterOpt = characterRepository.findByName(characterName);
        Optional<ItemPersistenceDTO> itemOpt = itemRepository.findByName(itemName);

        if (characterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        PlayerCharacterPersistenceDTO character = characterOpt.get();
        ItemPersistenceDTO item = itemOpt.get();

        character.addItem(item);
        PlayerCharacterPersistenceDTO updatedCharacter = characterRepository.save(character);

        return ResponseEntity.ok(adapter.fromPersistenceDTO(updatedCharacter));
    }

    @DeleteMapping(path = "/inventory/{itemName}")
    public ResponseEntity<PlayerCharacterDTO> removeItemFromInventory(@PathVariable String characterName, @PathVariable String itemName) {
        Optional<PlayerCharacterPersistenceDTO> characterOpt = characterRepository.findByName(characterName);
        Optional<ItemPersistenceDTO> itemOpt = itemRepository.findByName(itemName);

        if (characterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        PlayerCharacterPersistenceDTO character = characterOpt.get();
        ItemPersistenceDTO item = itemOpt.get();

        if (!character.getInventory().contains(item)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(adapter.fromPersistenceDTO(character));
        }

        character.removeItem(item);
        PlayerCharacterPersistenceDTO updatedCharacter = characterRepository.save(character);

        return ResponseEntity.ok(adapter.fromPersistenceDTO(updatedCharacter));
    }
}
