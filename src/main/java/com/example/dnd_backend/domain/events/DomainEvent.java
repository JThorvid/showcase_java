package com.example.dnd_backend.domain.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterCreated.class, name = CharacterCreated.TYPE),
        @JsonSubTypes.Type(value = CharacterUpdated.class, name = CharacterUpdated.TYPE),
        @JsonSubTypes.Type(value = ItemAdded.class, name = ItemAdded.TYPE),
        @JsonSubTypes.Type(value = ItemRemoved.class, name = ItemRemoved.TYPE),
})
public sealed interface DomainEvent permits CharacterCreated, CharacterUpdated, ItemAdded, ItemRemoved {
    String name();

    String getType();

    long timestamp();
}
