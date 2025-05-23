package com.example.dnd_backend.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ItemPersistenceDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private double weight;

    @ManyToMany(mappedBy = "inventory")
    private Set<PlayerCharacterPersistenceDTO> owners = new HashSet<>();

    public ItemPersistenceDTO() {
    }

    public ItemPersistenceDTO(String name, String description, double weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public ItemPersistenceDTO(long id, String name, String description, double weight) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Set<PlayerCharacterPersistenceDTO> getOwners() {
        return owners;
    }

    public void setOwners(Set<PlayerCharacterPersistenceDTO> owners) {
        this.owners = owners;
    }
} 