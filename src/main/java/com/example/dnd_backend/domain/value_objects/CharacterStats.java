package com.example.dnd_backend.domain.value_objects;

public record CharacterStats(
        int strength,
        int dexterity,
        int constitution,
        int intelligence,
        int wisdom,
        int charisma
) {
    public CharacterStats {
        if (strength > 25) throw new IllegalArgumentException("strength can never be more than 25");
        if (constitution > 25) throw new IllegalArgumentException("constitution can never be more than 25");
        if (dexterity > 20 || intelligence > 20 || wisdom > 20 || charisma > 20)
            throw new IllegalArgumentException("most stats can never be more than 20");
    }

    public CharacterStats() {
        this(10, 10, 10, 10, 10, 10);
    }
}