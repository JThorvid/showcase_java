package com.example.dnd_backend.gateway.api.errors;

public class InventoryCorruptException extends RuntimeException {
    public InventoryCorruptException(String message) {
        super(message);
    }
}
