package com.example.dnd_backend.persistence;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import org.springframework.stereotype.Component;

@Component
public class CharacterDTOAdapter {
    public PlayerCharacterDTO toPlayerCharacterDTO(PlayerCharacterPersistenceDTO persistenceDTO) {
        CharacterStats stats = persistenceDTO.getStats();
        return new PlayerCharacterDTO(
                persistenceDTO.getName(),
                stats
        );
    }

    public PlayerCharacterPersistenceDTO fromPlayerCharacterDTO(PlayerCharacterDTO playerCharacterDTO) {
        return new PlayerCharacterPersistenceDTO(
                playerCharacterDTO.name(),
                playerCharacterDTO.stats()
        );
    }
}
