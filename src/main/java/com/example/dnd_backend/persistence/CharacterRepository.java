package com.example.dnd_backend.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CharacterRepository extends CrudRepository<PlayerCharacterPersistenceDTO, Long> {
    Optional<PlayerCharacterPersistenceDTO> findByName(String name);
}
