package com.example.dnd_backend.gateway.api.errors;

public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(String itemName) {
        super("Item with name " + itemName + " already exists");
    }
}
