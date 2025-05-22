package com.example.dnd_backend.persistence;

import com.example.dnd_backend.controllers.CharacterStatsDTO;
import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import org.springframework.stereotype.Component;

@Component
public class PlayerCharacterDTOAdapter {
    public PlayerCharacterDTO toPlayerCharacterDTO(PlayerCharacterPersistenceDTO persistenceDTO) {
        CharacterStats stats = persistenceDTO.getStats();
        return new PlayerCharacterDTO(
                persistenceDTO.getName(),
                new CharacterStatsDTO(
                    stats.getStrength(),
                    stats.getDexterity(),
                    stats.getConstitution(),
                    stats.getIntelligence(),
                    stats.getWisdom(),
                    stats.getCharisma()
                )
        );
    }

    public PlayerCharacterPersistenceDTO fromPlayerCharacterDTO(PlayerCharacterDTO playerCharacterDTO) {
        CharacterStatsDTO stats = playerCharacterDTO.stats();
        return new PlayerCharacterPersistenceDTO(
                playerCharacterDTO.name(),
                stats.strength(),
                stats.dexterity(),
                stats.constitution(),
                stats.intelligence(),
                stats.wisdom(),
                stats.charisma()
        );
    }
}
