package com.example.dnd_backend.gateway.api.errors;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(String characterName) {
        super("Character with name " + characterName + " not found");
    }
}
