package com.example.dnd_backend.persistence;

import com.example.dnd_backend.controllers.ItemDTO;
import org.springframework.stereotype.Component;
import java.util.HashSet;

@Component
public class ItemDTOAdapter {
    public ItemDTO toItemDTO(ItemPersistenceDTO persistenceDTO) {
        return new ItemDTO(
            persistenceDTO.getName(),
            persistenceDTO.getDescription(),
            persistenceDTO.getWeight()
        );
    }

    public ItemPersistenceDTO fromItemDTO(ItemDTO itemDTO) {
        return new ItemPersistenceDTO(
            null,
            itemDTO.name(),
            itemDTO.description(),
            itemDTO.weight(),
            new HashSet<>()
        );
    }
} 