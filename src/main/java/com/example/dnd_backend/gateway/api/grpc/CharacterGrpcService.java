package com.example.dnd_backend.gateway.api.grpc;

import com.example.dnd_backend.gateway.api.dtos.CharacterDTO;
import com.example.dnd_backend.gateway.api.grpc.adapters.CharacterProtoAdapter;
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
        Character reply = CharacterProtoAdapter.toProto(characterDTO);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void getCharacters(EmptyRequest request,
                              StreamObserver<GetCharactersResponse> responseObserver) {
        log.info("Received request to retrieve all characters");
        List<CharacterDTO> characterDTOList = characterService.getCharacters();
        GetCharactersResponse.Builder builder = GetCharactersResponse.newBuilder();
        for (CharacterDTO characterDTO : characterDTOList) {
            builder.addCharacters(CharacterProtoAdapter.toProto(characterDTO));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateCharacter(Character character, StreamObserver<Character> responseObserver) {
        log.info("Received request to update character " + character.getName());
        CharacterDTO updatedCharacter = characterService.updateCharacter(CharacterProtoAdapter.fromProto(character));
        responseObserver.onNext(CharacterProtoAdapter.toProto(updatedCharacter));
        responseObserver.onCompleted();
    }

    @Override
    public void createCharacter(Character character, StreamObserver<Character> responseObserver) {
        log.info("Received request to create character " + character.getName());
        CharacterDTO characterDTO = characterService.createCharacter(CharacterProtoAdapter.fromProto(character));
        responseObserver.onNext(CharacterProtoAdapter.toProto(characterDTO));
        responseObserver.onCompleted();
    }
}