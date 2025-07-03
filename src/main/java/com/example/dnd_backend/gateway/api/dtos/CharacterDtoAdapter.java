package com.example.dnd_backend.gateway.api.dtos;

import com.example.dnd_backend.domain.aggregates.PlayerCharacter;
import com.example.dnd_backend.domain.value_objects.CharacterStats;

public class CharacterDtoAdapter {
    private CharacterDtoAdapter() {
    }

    public static CharacterDTO characterToDto(PlayerCharacter character) {
        return new CharacterDTO(
                character.getName(),
                character.getStats().strength(),
                character.getStats().dexterity(),
                character.getStats().constitution(),
                character.getStats().intelligence(),
                character.getStats().wisdom(),
                character.getStats().charisma());
    }

    public static PlayerCharacter dtoToCharacter(CharacterDTO dto) {
        return new PlayerCharacter(
                dto.name(),
                new CharacterStats(
                        dto.strength(),
                        dto.dexterity(),
                        dto.constitution(),
                        dto.intelligence(),
                        dto.wisdom(),
                        dto.charisma()
                )
        );
    }

}
