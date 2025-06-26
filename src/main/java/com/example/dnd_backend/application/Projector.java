package com.example.dnd_backend.application;

import com.example.dnd_backend.domain.events.DomainEvent;

import java.util.List;
import java.util.Optional;

public interface Projector<T> {
    Optional<T> getByName(String name);

    List<T> getAll();

    boolean exists(String name);

    void processEvent(DomainEvent event);
}
