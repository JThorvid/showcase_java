package com.example.dnd_backend.test;

import com.example.dnd_backend.controllers.PlayerCharacterDTO;
import org.springframework.web.client.RestTemplate;

public class CharacterTestClient {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        
        PlayerCharacterDTO character = new PlayerCharacterDTO(
            "Gandalf",
            10, 14, 16, 18, 16, 12
        );

        PlayerCharacterDTO response = restTemplate.postForObject(
            "http://localhost:8080/characters",
            character,
            PlayerCharacterDTO.class
        );

        System.out.println("Created character: " + response);
    }
}
