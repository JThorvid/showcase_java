package com.example.dnd_backend.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterCreated.class, name = "CHARACTER_CREATED"),
        @JsonSubTypes.Type(value = CharacterUpdated.class, name = "CHARACTER_UPDATED"),
        @JsonSubTypes.Type(value = ItemAdded.class, name = "ITEM_ADDED"),
        @JsonSubTypes.Type(value = ItemRemoved.class, name = "ITEM_REMOVED")
})
public abstract class DomainEvent {
    private final String name;
    private final long timestamp;
    private final String type;

    protected DomainEvent() {
        this.name = "";
        this.timestamp = System.currentTimeMillis();
        this.type = "";
    }

    protected DomainEvent(String name, String type) {
        this.name = name;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
    }
}
