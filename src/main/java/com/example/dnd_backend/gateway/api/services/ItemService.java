package com.example.dnd_backend.gateway.api.services;

import com.example.dnd_backend.application.Projector;
import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.events.ItemDestroyed;
import com.example.dnd_backend.domain.events.ItemUpdated;
import com.example.dnd_backend.domain.value_objects.Item;
import com.example.dnd_backend.gateway.api.errors.ItemAlreadyExistsException;
import com.example.dnd_backend.gateway.api.errors.ItemIsOwnedException;
import com.example.dnd_backend.gateway.api.errors.ItemNotFoundException;
import com.example.dnd_backend.gateway.eventstore.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final EventRepository eventStore;
    private final Projector<Item> itemManager;
    private final Projector<PlayerCharacter> characterManager;

    public List<Item> getItems() {
        return itemManager.getAll();
    }

    public Item getItem(String name) throws ItemNotFoundException {
        Optional<Item> item = itemManager.getByName(name);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new ItemNotFoundException(name);
        }
    }

    public Item createItem(Item item) throws ItemAlreadyExistsException {
        if (itemManager.exists(item.name())) {
            throw new ItemAlreadyExistsException(item.name());
        }
        ItemCreated event = new ItemCreated(item);
        eventStore.sendEvent(event);
        return item;
    }

    public Item updateItem(Item item) throws ItemNotFoundException {
        if (!itemManager.exists(item.name())) {
            throw new ItemNotFoundException(item.name());
        }
        ItemUpdated event = new ItemUpdated(item);
        eventStore.sendEvent(event);
        return item;
    }

    public void deleteItem(String name) throws ItemNotFoundException, ItemIsOwnedException {
        Item item = itemManager.getByName(name).orElseThrow(() -> new ItemNotFoundException(name));
        List<PlayerCharacter> owningCharacters = charactersThatOwnThisItem(item);
        if (!owningCharacters.isEmpty()) {
            throw new ItemIsOwnedException(name, owningCharacters.stream().map(PlayerCharacter::getName).toList());
        }
        eventStore.sendEvent(new ItemDestroyed(item));
    }


    private List<PlayerCharacter> charactersThatOwnThisItem(Item item) {
        List<PlayerCharacter> characters = new ArrayList<>();
        for (PlayerCharacter player : characterManager.getAll()) {
            if (player.getInventory().getCountPerItem().containsKey(item.name()))
                characters.add(player);
        }
        return characters;
    }
}
