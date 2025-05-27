package com.example.dnd_backend.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor // Generates a constructor with no arguments
@AllArgsConstructor // Generates a constructor with arguments for all fields
@EqualsAndHashCode(of = {"name"}) // Base equals/hashCode on name
public class PlayerCharacterPersistenceDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Embedded
    private CharacterStats stats;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "character_inventory",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    // @EqualsAndHashCode.Exclude // No longer needed if class-level specifies `of` for a different field.
    private Set<ItemPersistenceDTO> inventory = new HashSet<>();

    // Lombok's @AllArgsConstructor will cover the case with all fields including id, name, stats, inventory.
    // A constructor for new entities (without id and inventory) might be useful.
    // Let's create one specifically for name and stats, as inventory is usually managed via addItem/removeItem.
    public PlayerCharacterPersistenceDTO(String name, CharacterStats stats) {
        this.name = name;
        this.stats = stats;
        // inventory is initialized to empty set by default
    }
    
    // Constructor for tests or full manual setup, now covered by @AllArgsConstructor if id and inventory are included.
    // For compatibility, this specific one is useful if not all args are always provided by @AllArgsConstructor.
    // Given @AllArgsConstructor takes all fields, this one becomes redundant if all fields are used.
    // Let's remove this if @AllArgs covers the previous manual version (1L, "Aragorn", 15,18,14,13,13,16)
    // The stats are now an object, so the direct int constructor is no longer applicable in this form.
    // public PlayerCharacterPersistenceDTO(Long id, String name, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
    //     this.id = id;
    //     this.name = name;
    //     this.stats = new CharacterStats(strength, dexterity, constitution, intelligence, wisdom, charisma);
    // }

    // Helper methods for managing the bidirectional relationship with ItemPersistenceDTO
    public void addItem(ItemPersistenceDTO item) {
        this.inventory.add(item);
        item.getOwners().add(this);
    }

    public void removeItem(ItemPersistenceDTO item) {
        this.inventory.remove(item);
        item.getOwners().remove(this);
    }

    // Getters, setters for id, name, stats, inventory, and individual stat fields are handled by @Data
    // or by delegation if we keep the delegated stat getters/setters.
    // With @Data on this class and CharacterStats, direct access to stats.getStrength() etc. is standard.
    // Keeping the delegated methods can be a choice for API consistency if previously used.
    // For now, removing them to rely on stats.get... directly via playerChar.getStats().getStrength().
}
