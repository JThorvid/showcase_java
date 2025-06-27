package com.example.dnd_backend.gateway.eventstore;

import com.example.dnd_backend.domain.events.DomainEvent;

public interface EventRepository {
    void sendEvent(DomainEvent event);
}
