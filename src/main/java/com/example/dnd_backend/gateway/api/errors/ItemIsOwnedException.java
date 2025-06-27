package com.example.dnd_backend.gateway.api.errors;

import java.util.List;

public class ItemIsOwnedException extends RuntimeException {
    public ItemIsOwnedException(String itemName, List<String> characters) {
        super(String.format("The item %s is currently owned by these characters: %s\n" +
                        "You have to remove the item from their inventories before you can proceed.",
                itemName, characters));
    }
}
