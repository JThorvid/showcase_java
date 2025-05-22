package com.example.dnd_backend.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Embedded;

@Entity
public class PlayerCharacterPersistenceDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    
    @Embedded
    private CharacterStats stats;

    public PlayerCharacterPersistenceDTO(String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this.name = name;
        this.stats = new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    public PlayerCharacterPersistenceDTO(long id, String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this.id = id;
        this.name = name;
        this.stats = new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    public PlayerCharacterPersistenceDTO() {
        this.stats = new CharacterStats();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return stats.getStrength();
    }

    public void setStrength(int strength) {
        stats.setStrength(strength);
    }

    public int getDexterity() {
        return stats.getDexterity();
    }

    public void setDexterity(int dexterity) {
        stats.setDexterity(dexterity);
    }

    public int getConstitution() {
        return stats.getConstitution();
    }

    public void setConstitution(int constitution) {
        stats.setConstitution(constitution);
    }

    public int getIntelligence() {
        return stats.getIntelligence();
    }

    public void setIntelligence(int intelligence) {
        stats.setIntelligence(intelligence);
    }

    public int getWisdom() {
        return stats.getWisdom();
    }

    public void setWisdom(int wisdom) {
        stats.setWisdom(wisdom);
    }

    public int getCharisma() {
        return stats.getCharisma();
    }

    public void setCharisma(int charisma) {
        stats.setCharisma(charisma);
    }

    public CharacterStats getStats() {
        return stats;
    }

    public void setStats(CharacterStats stats) {
        this.stats = stats;
    }
}
