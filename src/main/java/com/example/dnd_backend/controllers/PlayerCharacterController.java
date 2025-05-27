package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
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
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/characters")
public class PlayerCharacterController {
    private final CharacterRepository characterRepository;
    private final PlayerCharacterDTOAdapter adapter;
    private final ItemDTOAdapter itemAdapter;
    private final ItemRepository itemRepository;

    public PlayerCharacterController(CharacterRepository characterRepository, PlayerCharacterDTOAdapter adapter, ItemDTOAdapter itemAdapter, ItemRepository itemRepository) {
        this.characterRepository = characterRepository;
        this.adapter = adapter;
        this.itemAdapter = itemAdapter;
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<PlayerCharacterDTO> getCharacters() {
        return StreamSupport.stream(characterRepository.findAll().spliterator(), false)
                .map(adapter::toPlayerCharacterDTO).toList();
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<PlayerCharacterDTO> getCharacter(@PathVariable String name) {
        return characterRepository.findByName(name)
                .map(adapter::toPlayerCharacterDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{name}/items")
    public ResponseEntity<List<ItemDTO>> getCharacterInventory(@PathVariable String name) {
        Optional<PlayerCharacterPersistenceDTO> characterPersistenceDTO = characterRepository.findByName(name);
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

    @PostMapping
    public ResponseEntity<PlayerCharacterDTO> createCharacter(@RequestBody PlayerCharacterDTO characterDTO) {
        PlayerCharacterPersistenceDTO toSave = adapter.fromPlayerCharacterDTO(characterDTO);
        PlayerCharacterPersistenceDTO saved = characterRepository.save(
                toSave
        );
        PlayerCharacterDTO character = adapter.toPlayerCharacterDTO(
                saved
        );
        return ResponseEntity.ok(character);
    }

    @PostMapping(path = "/{characterName}/inventory/{itemName}")
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

        return ResponseEntity.ok(adapter.toPlayerCharacterDTO(updatedCharacter));
    }

    @DeleteMapping(path = "/{characterName}/inventory/{itemName}")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(adapter.toPlayerCharacterDTO(character));
        }

        character.removeItem(item);
        PlayerCharacterPersistenceDTO updatedCharacter = characterRepository.save(character);

        return ResponseEntity.ok(adapter.toPlayerCharacterDTO(updatedCharacter));
    }
}
