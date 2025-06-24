package com.example.dnd_backend.domain.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterCreated.class, name = CharacterCreated.TYPE),
        @JsonSubTypes.Type(value = CharacterUpdated.class, name = CharacterUpdated.TYPE),
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
