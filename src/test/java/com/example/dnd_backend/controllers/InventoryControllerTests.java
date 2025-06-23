package com.example.dnd_backend.controllers;

import com.example.dnd_backend.entities.CharacterStats;
import com.example.dnd_backend.entities.ItemDTO;
import com.example.dnd_backend.entities.PlayerCharacter;
import com.example.dnd_backend.gateway.controllers.InventoryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@WebMvcTest(InventoryController.class)
class InventoryControllerTests {
//    @MockitoBean
//    private CharacterRepository characterRepository;

//    @MockitoBean
//    private ItemRepository itemRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacterInventory() throws Exception {
        String characterName = "Gandalf";

        CharacterStats gandalfStats = new CharacterStats(10, 10, 10, 18, 18, 14);
//        PlayerCharacterPersistenceDTO gandalfPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, gandalfStats, new HashSet<>());
//        gandalfPersistence.addItem(staffPersistence);
//        gandalfPersistence.addItem(pipePersistence);

        ItemDTO staffDTO = new ItemDTO("Staff of Power", "A mighty staff", 2.0);
        ItemDTO pipeDTO = new ItemDTO("Longbottom Leaf Pipe", "A fine pipe", 0.2);

//        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(gandalfPersistence));

        mockMvc.perform(get("/characters/{characterName}/inventory", characterName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].name", containsInAnyOrder("Staff of Power", "Longbottom Leaf Pipe")))
                .andExpect(jsonPath("$.[?(@.name == 'Staff of Power')].description").value(hasItem("A mighty staff")))
                .andExpect(jsonPath("$.[?(@.name == 'Staff of Power')].weight").value(hasItem(2.0)))
                .andExpect(jsonPath("$.[?(@.name == 'Longbottom Leaf Pipe')].description").value(hasItem("A fine pipe")))
                .andExpect(jsonPath("$.[?(@.name == 'Longbottom Leaf Pipe')].weight").value(hasItem(0.2)));

//        verify(characterRepository).findByName(characterName);
    }

    @Test
    void testGetCharacterInventory_characterNotFound() throws Exception {
        String characterName = "Bilbo";
//        when(characterRepository.findByName(characterName)).thenReturn(Optional.empty());

        mockMvc.perform(get("/characters/{characterName}/inventory", characterName))
                .andExpect(status().isNotFound());
//        verify(characterRepository).findByName(characterName);
    }

    @Test
    void testAddItemToInventory() throws Exception {
        String characterName = "Aragorn";
        String itemName = "Anduril";
        CharacterStats aragornStats = new CharacterStats(15, 18, 14, 13, 13, 16);

//        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(aragornPersistence));
//        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(andurilPersistence));
//        when(characterRepository.save(any(PlayerCharacterPersistenceDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerCharacter aragornDTO = new PlayerCharacter(characterName, aragornStats);

        mockMvc.perform(post("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(characterName))
                .andExpect(jsonPath("$.stats.strength").value(15));

//        verify(characterRepository).findByName(characterName);
//        verify(itemRepository).findByName(itemName);
//        verify(characterRepository).save(aragornPersistence);
//        assertEquals(1, aragornPersistence.getInventory().size());
//        assertTrue(aragornPersistence.getInventory().contains(andurilPersistence));
    }

    @Test
    void testAddItemToInventory_characterNotFound() throws Exception {
//        when(characterRepository.findByName("NonExistent")).thenReturn(Optional.empty());
//        when(itemRepository.findByName("SomeItem")).thenReturn(Optional.of(new ItemPersistenceDTO(1L, "SomeItem", "Desc", 1.0, new HashSet<>())));

        mockMvc.perform(post("/characters/NonExistent/inventory/SomeItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddItemToInventory_itemNotFound() throws Exception {
        CharacterStats someStats = new CharacterStats(10, 10, 10, 10, 10, 10);
//        when(characterRepository.findByName("SomeCharacter")).thenReturn(Optional.of(new PlayerCharacterPersistenceDTO(1L, "SomeCharacter", someStats, new HashSet<>())));
//        when(itemRepository.findByName("NonExistentItem")).thenReturn(Optional.empty());

        mockMvc.perform(post("/characters/SomeCharacter/inventory/NonExistentItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromInventory() throws Exception {
        String characterName = "Legolas";
        String itemName = "Bow of the Galadhrim";
        CharacterStats legolasStats = new CharacterStats(12, 20, 12, 15, 16, 14);
        PlayerCharacter legolasDTO = new PlayerCharacter(characterName, legolasStats);
//        legolasDTO.addItem(bowPersistence);

//        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(legolasPersistence));
//        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(bowPersistence));
//        when(characterRepository.save(any(PlayerCharacterPersistenceDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(delete("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(characterName))
                .andExpect(jsonPath("$.stats.dexterity").value(20));

//        verify(characterRepository).findByName(characterName);
//        verify(itemRepository).findByName(itemName);
//        verify(characterRepository).save(legolasPersistence);
//        assertEquals(0, legolasPersistence.getInventory().size());
//        assertFalse(legolasPersistence.getInventory().contains(bowPersistence));
    }

    @Test
    void testRemoveItemFromInventory_itemNotInInventory() throws Exception {
        String characterName = "Gimli";
        String itemName = "Battle Axe";
        CharacterStats gimliStats = new CharacterStats(16, 14, 18, 10, 12, 10);

//        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(gimliPersistence));
//        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(axePersistence));

        PlayerCharacter gimliDTO = new PlayerCharacter(characterName, gimliStats);

        mockMvc.perform(delete("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isBadRequest());

//        verify(characterRepository).findByName(characterName);
//        verify(itemRepository).findByName(itemName);
//        verify(characterRepository, never()).save(any(PlayerCharacterPersistenceDTO.class));
    }
}
