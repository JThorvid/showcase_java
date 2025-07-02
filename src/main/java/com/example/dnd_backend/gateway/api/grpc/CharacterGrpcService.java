package com.example.dnd_backend.gateway.api.grpc;

import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.services.CharacterService;
import com.example.dnd_backend.proto.Character;
import com.example.dnd_backend.proto.CharacterNameRequest;
import com.example.dnd_backend.proto.CharacterServiceGrpc.CharacterServiceImplBase;
import com.example.dnd_backend.proto.EmptyRequest;
import com.example.dnd_backend.proto.GetCharactersResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterGrpcService extends CharacterServiceImplBase {
    private static final Log log = LogFactory.getLog(CharacterGrpcService.class);
    private final CharacterService characterService;

    @Override
    public void getCharacter(CharacterNameRequest request,
                             StreamObserver<Character> responseObserver) {
        log.info("Received request to retrieve " + request.getName());
        CharacterDTO characterDTO = characterService.getCharacter(request.getName());
        Character reply = Character.newBuilder()
                .setName(characterDTO.name())
                .setStrength(characterDTO.strength())
                .setDexterity(characterDTO.dexterity())
                .setConstitution(characterDTO.constitution())
                .setIntelligence(characterDTO.intelligence())
                .setWisdom(characterDTO.wisdom())
                .setCharisma(characterDTO.charisma())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void getCharacters(EmptyRequest request,
                              StreamObserver<GetCharactersResponse> responseObserver) {
        log.info("Received request to retrieve all characters");
        List<CharacterDTO> characterDTOList = characterService.getCharacters();
//        GetCharactersResponse response = GetCharactersResponse.newBuilder()
//                .setCharacters()
    }
}