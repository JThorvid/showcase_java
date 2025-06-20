package com.example.dnd_backend.test;

import com.example.dnd_backend.controllers.PlayerCharacter;
import org.springframework.web.client.RestTemplate;

public class CharacterTestClient {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        PlayerCharacter character = new PlayerCharacter(
                "Gandalf",
                10, 14, 16, 18, 16, 12
        );

        PlayerCharacter response = restTemplate.postForObject(
                "http://localhost:8080/characters",
                character,
                PlayerCharacter.class
        );

        System.out.println("Created character: " + response);
    }
}
