package com.example.dnd_backend.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ItemRepository extends CrudRepository<ItemPersistenceDTO, Long> {
    Optional<ItemPersistenceDTO> findByName(String name);
} 