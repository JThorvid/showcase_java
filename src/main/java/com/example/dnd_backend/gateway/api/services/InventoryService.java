package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.entities.Inventory;
import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.events.ItemAdded;
import com.example.dnd_backend.domain.events.ItemRemoved;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.dtos.ItemCommandDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDTO;
import com.example.dnd_backend.gateway.api.dtos.ItemDtoAdapter;
import com.example.dnd_backend.gateway.api.errors.CharacterNotFoundException;
import com.example.dnd_backend.gateway.api.errors.InventoryCorruptException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.eventstore.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final EventRepository eventStore;
    private final Projector<PlayerCharacter> characterManager;
    private final Projector<Item> itemManager;
    private final ItemDtoAdapter adapter;

    public List<ItemDTO> getInventoryFor(String characterName) throws InventoryCorruptException, CharacterNotFoundException {
        Optional<PlayerCharacter> character = characterManager.getByName(characterName);
        if (character.isPresent()) {
            List<ItemDTO> inventoryDTO = new ArrayList<>();
            Inventory inventory = character.get().getInventory();
            for (String itemName : inventory.getCountPerItem().keySet()) {
                Optional<Item> item = itemManager.getByName(itemName);
                if (item.isEmpty()) {
                    String message = String.format("The inventory is corrupt. %s is in the inventory but does not exist.", itemName);
                    throw new InventoryCorruptException(message);
                } else {
                    Item thisItem = item.get();

                    inventoryDTO.add(adapter.itemToDto(thisItem, inventory.getCountPerItem().get(itemName)));
                }
            }
            return inventoryDTO;
        } else {
            throw new CharacterNotFoundException(characterName);
        }
    }

    public ItemCommandDTO addToInventoryFor(String characterName, ItemCommandDTO itemCommandDTO) throws ItemNotFoundException, CharacterNotFoundException {
        return processItemRequest(characterName, itemCommandDTO, AddOrRemove.ADD);
    }

    public ItemCommandDTO removeFromInventoryFor(String characterName, ItemCommandDTO itemCommandDTO) throws ItemNotFoundException, CharacterNotFoundException {
        return processItemRequest(characterName, itemCommandDTO, AddOrRemove.REMOVE);
    }

    private ItemCommandDTO processItemRequest(String characterName, ItemCommandDTO itemCommandDTO, AddOrRemove addOrRemove) {
        Optional<PlayerCharacter> character = characterManager.getByName(characterName);
        if (character.isEmpty()) {
            throw new CharacterNotFoundException(characterName);
        }

        Optional<Item> item = itemManager.getByName(itemCommandDTO.name());
        if (item.isEmpty()) {
            throw new ItemNotFoundException(itemCommandDTO.name());
        }

        DomainEvent event;
        switch (addOrRemove) {
            case ADD -> event = new ItemAdded(characterName, item.get(), itemCommandDTO.quantity());
            case REMOVE -> event = new ItemRemoved(characterName, item.get(), itemCommandDTO.quantity());
            default -> throw new IllegalArgumentException("Unknown operation: " + addOrRemove);
        }
        eventStore.sendEvent(event);

        return itemCommandDTO;
    }

}
