package com.example.dnd_backend.gateway.api.dtos;

import com.example.dnd_backend.domain.value_objects.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoAdapter {
    public ItemCommandDTO itemToCommandDto(Item item, int quantity) {
        return new ItemCommandDTO(item.name(), quantity);
    }

    public ItemDTO itemToDto(Item item, int quantity) {
        return new ItemDTO(item.name(), item.description(), item.weight(), quantity);
    }
}
