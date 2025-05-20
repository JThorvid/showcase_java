package com.example.dnd_backend.persistence;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import org.springframework.stereotype.Component;

@Component
public class PlayerCharacterDTOAdapter {
    public PlayerCharacterDTO toPlayerCharacterDTO(PlayerCharacterPersistenceDTO persistenceDTO) {
        return new PlayerCharacterDTO(
                persistenceDTO.getName(), persistenceDTO.getStrength(), persistenceDTO.getDexterity(), persistenceDTO.getConstitution(), persistenceDTO.getWisdom(), persistenceDTO.getIntelligence(), persistenceDTO.getCharisma()
        );
    }

    public PlayerCharacterPersistenceDTO fromPlayerCharacterDTO(PlayerCharacterDTO playerCharacterDTO) {
        return new PlayerCharacterPersistenceDTO(
                playerCharacterDTO.name(), playerCharacterDTO.strength(), playerCharacterDTO.dexterity(), playerCharacterDTO.constitution(), playerCharacterDTO.intelligence(), playerCharacterDTO.wisdom(), playerCharacterDTO.charisma()
        );
    }
}
