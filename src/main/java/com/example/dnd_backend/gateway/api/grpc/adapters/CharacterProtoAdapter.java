package com.example.dnd_backend.gateway.api.grpc.adapters;

import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.proto.Character;

public class CharacterProtoAdapter {
    private CharacterProtoAdapter() {
    }

    public static Character toProto(CharacterDTO dto) {
        return Character.newBuilder()
                .setName(dto.name())
                .setStrength(dto.strength())
                .setDexterity(dto.dexterity())
                .setConstitution(dto.constitution())
                .setIntelligence(dto.intelligence())
                .setWisdom(dto.wisdom())
                .setCharisma(dto.charisma())
                .build();
    }

    public static CharacterDTO fromProto(Character proto) {
        return new CharacterDTO(
                proto.getName(),
                proto.getStrength(),
                proto.getDexterity(),
                proto.getConstitution(),
                proto.getIntelligence(),
                proto.getWisdom(),
                proto.getCharisma()
        );
    }
}
