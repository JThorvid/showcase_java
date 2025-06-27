package com.example.dnd_backend.gateway.api.errors;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String itemName) {
        super("Item not found: " + itemName);
    }
}
