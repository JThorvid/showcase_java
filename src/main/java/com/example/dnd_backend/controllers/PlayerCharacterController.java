package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public PlayerCharacterController(CharacterRepository characterRepository, PlayerCharacterDTOAdapter adapter, ItemDTOAdapter itemAdapter) {
        this.characterRepository = characterRepository;
        this.adapter = adapter;
        this.itemAdapter = itemAdapter;
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
}
