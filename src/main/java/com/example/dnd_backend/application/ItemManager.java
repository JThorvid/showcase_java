package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.events.DomainEvent;
import com.example.dnd_backend.domain.events.ItemCreated;
import com.example.dnd_backend.domain.value_objects.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemManager implements Projector<Item> {
    private final List<Item> items = new ArrayList<>();

    @Override
    public Optional<Item> getByName(String name) {
        return items.stream().filter(item -> item.name().equals(name)).findFirst();
    }

    @Override
    public List<Item> getAll() {
        return items;
    }

    @Override
    public boolean exists(String name) {
        return items.stream().anyMatch(item -> item.name().equals(name));
    }

    @Override
    public void processEvent(DomainEvent event) {
        if (event instanceof ItemCreated e) {
            items.add(e.item());
        }
    }
}
