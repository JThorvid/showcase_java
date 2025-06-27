package com.example.dnd_backend.gateway.api.dtos;

import java.util.Objects;

public record ItemDTO(String name, String description, double weight, int quantity) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDTO(String thatName, String thatDescription, double thatWeight, int thatQuantity)))
            return false;

        if (!Objects.equals(name, thatName)) return false;
        if (!Objects.equals(description, thatDescription)) return false;
        if (!Objects.equals(weight, thatWeight)) return false;
        return Objects.equals(quantity, thatQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, weight, quantity);
    }
}
