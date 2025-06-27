package com.example.dnd_backend.gateway.api.errors;

public class CharacterAlreadyExistsException extends RuntimeException {
    public CharacterAlreadyExistsException(String characterName) {
        super("Character with name " + characterName + " already exists");
    }
}
