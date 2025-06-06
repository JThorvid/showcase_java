package com.example.dnd_backend.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterCreated.class, name = "CHARACTER_CREATED"),
        @JsonSubTypes.Type(value = CharacterUpdated.class, name = "CHARACTER_UPDATED"),
        @JsonSubTypes.Type(value = ItemAdded.class, name = "ITEM_ADDED"),
        @JsonSubTypes.Type(value = ItemRemoved.class, name = "ITEM_REMOVED")
})
public abstract class CharacterEvent {
    private final String characterName;
    private final long timestamp;
    private final String type;

    public CharacterEvent(String characterName, String type) {
        this.characterName = characterName;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
    }

    public String getCharacterName() {
        return characterName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
}
