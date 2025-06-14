package com.example.dnd_backend.persistence;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterDTOAdapter {
    public PlayerCharacterPersistenceDTO toPersistenceDTO(PlayerCharacterDTO dto) {
        return new PlayerCharacterPersistenceDTO(
                dto.getName(),
                new CharacterStats(
                    dto.getStats().getStrength(),
                    dto.getStats().getDexterity(),
                    dto.getStats().getConstitution(),
                    dto.getStats().getIntelligence(),
                    dto.getStats().getWisdom(),
                    dto.getStats().getCharisma()
                )
        );
    }

    public PlayerCharacterDTO fromPersistenceDTO(PlayerCharacterPersistenceDTO dto) {
        return new PlayerCharacterDTO(
                dto.getName(),
                new CharacterStats(
                    dto.getStats().getStrength(),
                    dto.getStats().getDexterity(),
                    dto.getStats().getConstitution(),
                    dto.getStats().getIntelligence(),
                    dto.getStats().getWisdom(),
                    dto.getStats().getCharisma()
                )
        );
    }
}
