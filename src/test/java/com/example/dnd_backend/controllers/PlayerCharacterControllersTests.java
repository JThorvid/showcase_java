package com.example.dnd_backend.controllers;

import com.example.dnd_backend.persistence.CharacterRepository;
import com.example.dnd_backend.persistence.PlayerCharacterDTOAdapter;
import com.example.dnd_backend.persistence.PlayerCharacterPersistenceDTO;
import com.example.dnd_backend.persistence.ItemPersistenceDTO;
import com.example.dnd_backend.persistence.ItemDTOAdapter;
import com.example.dnd_backend.persistence.ItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@WebMvcTest(PlayerCharacterController.class)
class PlayerCharacterControllersTests {
    @MockitoBean
    private CharacterRepository characterRepository;

    @MockitoBean
    private PlayerCharacterDTOAdapter adapter;

    @MockitoBean
    private ItemDTOAdapter itemAdapter;

    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCharacters() throws Exception {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 
            new CharacterStatsDTO(18, 8, 16, 12, 8, 19)
        );
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        when(characterRepository.findAll()).thenReturn(List.of(persistenceDTO));

        mockMvc.perform(get("/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[0].strength").value(18));
    }

    @Test
    void testGetCharacter() throws Exception {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 
            new CharacterStatsDTO(18, 8, 16, 12, 8, 19)
        );
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        when(characterRepository.findByName("Bob")).thenReturn(Optional.of(persistenceDTO));

        mockMvc.perform(get("/characters/Bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.strength").value(18));
    }

    @Test
    void testCreateCharacter() {
        PlayerCharacterDTO playerCharacterDTO = new PlayerCharacterDTO("Bob", 
            new CharacterStatsDTO(18, 8, 16, 12, 8, 19)
        );
        PlayerCharacterPersistenceDTO persistenceDTO = new PlayerCharacterPersistenceDTO(1L, "Bob", 18, 8, 16, 12, 8, 19);

        when(adapter.fromPlayerCharacterDTO(playerCharacterDTO)).thenReturn(persistenceDTO);
        when(adapter.toPlayerCharacterDTO(persistenceDTO)).thenReturn(playerCharacterDTO);

        Mockito.when(characterRepository.save(persistenceDTO)).thenReturn(persistenceDTO);

        PlayerCharacterController controller = new PlayerCharacterController(characterRepository, adapter, itemAdapter, itemRepository);

        ResponseEntity<PlayerCharacterDTO> actual = controller.createCharacter(playerCharacterDTO);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(playerCharacterDTO, actual.getBody());

        verify(adapter).fromPlayerCharacterDTO(playerCharacterDTO);
        verify(characterRepository).save(persistenceDTO);
        verify(adapter).toPlayerCharacterDTO(persistenceDTO);
        verifyNoMoreInteractions(characterRepository);
    }

    @Test
    void testGetCharacterInventory() throws Exception {
        String characterName = "Gandalf";
        ItemPersistenceDTO staffPersistence = new ItemPersistenceDTO(1L, "Staff of Power", "A mighty staff", 2.0);
        ItemPersistenceDTO pipePersistence = new ItemPersistenceDTO(2L, "Longbottom Leaf Pipe", "A fine pipe", 0.2);
        PlayerCharacterPersistenceDTO gandalfPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, 10,10,10,18,18,14);
        gandalfPersistence.getInventory().add(staffPersistence);
        gandalfPersistence.getInventory().add(pipePersistence);

        ItemDTO staffDTO = new ItemDTO("Staff of Power", "A mighty staff", 2.0);
        ItemDTO pipeDTO = new ItemDTO("Longbottom Leaf Pipe", "A fine pipe", 0.2);
        
        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(gandalfPersistence));
        when(itemAdapter.toItemDTO(staffPersistence)).thenReturn(staffDTO);
        when(itemAdapter.toItemDTO(pipePersistence)).thenReturn(pipeDTO);

        mockMvc.perform(get("/characters/{name}/items", characterName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].name", containsInAnyOrder("Staff of Power", "Longbottom Leaf Pipe")))
                .andExpect(jsonPath("$.[?(@.name == 'Staff of Power')].description").value(hasItem("A mighty staff")))
                .andExpect(jsonPath("$.[?(@.name == 'Staff of Power')].weight").value(hasItem(2.0)))
                .andExpect(jsonPath("$.[?(@.name == 'Longbottom Leaf Pipe')].description").value(hasItem("A fine pipe")))
                .andExpect(jsonPath("$.[?(@.name == 'Longbottom Leaf Pipe')].weight").value(hasItem(0.2)));

        verify(characterRepository).findByName(characterName);
        verify(itemAdapter).toItemDTO(staffPersistence);
        verify(itemAdapter).toItemDTO(pipePersistence);
    }

    @Test
    void testGetCharacterInventory_characterNotFound() throws Exception {
        String characterName = "Bilbo";
        when(characterRepository.findByName(characterName)).thenReturn(Optional.empty());

        mockMvc.perform(get("/characters/{name}/items", characterName))
                .andExpect(status().isNotFound());
        verify(characterRepository).findByName(characterName);
    }

    @Test
    void testAddItemToInventory() throws Exception {
        String characterName = "Aragorn";
        String itemName = "Anduril";
        PlayerCharacterPersistenceDTO aragornPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, 15,18,14,13,13,16);
        ItemPersistenceDTO andurilPersistence = new ItemPersistenceDTO(1L, itemName, "Flame of the West", 3.0);

        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(aragornPersistence));
        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(andurilPersistence));
        when(characterRepository.save(any(PlayerCharacterPersistenceDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        PlayerCharacterDTO aragornDTO = new PlayerCharacterDTO(
            aragornPersistence.getName(), 
            new CharacterStatsDTO(aragornPersistence.getStrength(), aragornPersistence.getDexterity(), aragornPersistence.getConstitution(), aragornPersistence.getIntelligence(), aragornPersistence.getWisdom(), aragornPersistence.getCharisma())
        );
        when(adapter.toPlayerCharacterDTO(aragornPersistence)).thenReturn(aragornDTO);

        mockMvc.perform(post("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(characterName))
                .andExpect(jsonPath("$.strength").value(15));

        verify(characterRepository).findByName(characterName);
        verify(itemRepository).findByName(itemName);
        verify(characterRepository).save(aragornPersistence);
        assertEquals(1, aragornPersistence.getInventory().size());
        assertTrue(aragornPersistence.getInventory().contains(andurilPersistence));
    }

    @Test
    void testAddItemToInventory_characterNotFound() throws Exception {
        when(characterRepository.findByName("NonExistent")).thenReturn(Optional.empty());
        when(itemRepository.findByName("SomeItem")).thenReturn(Optional.of(new ItemPersistenceDTO()));

        mockMvc.perform(post("/characters/NonExistent/inventory/SomeItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddItemToInventory_itemNotFound() throws Exception {
        when(characterRepository.findByName("SomeCharacter")).thenReturn(Optional.of(new PlayerCharacterPersistenceDTO()));
        when(itemRepository.findByName("NonExistentItem")).thenReturn(Optional.empty());

        mockMvc.perform(post("/characters/SomeCharacter/inventory/NonExistentItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromInventory() throws Exception {
        String characterName = "Legolas";
        String itemName = "Bow of the Galadhrim";
        PlayerCharacterPersistenceDTO legolasPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, 12,20,12,15,16,14);
        ItemPersistenceDTO bowPersistence = new ItemPersistenceDTO(1L, itemName, "A fine bow", 2.0);
        legolasPersistence.addItem(bowPersistence);

        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(legolasPersistence));
        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(bowPersistence));
        when(characterRepository.save(any(PlayerCharacterPersistenceDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerCharacterDTO legolasDTO = new PlayerCharacterDTO(
            legolasPersistence.getName(), 
            new CharacterStatsDTO(legolasPersistence.getStrength(), legolasPersistence.getDexterity(), legolasPersistence.getConstitution(), legolasPersistence.getIntelligence(), legolasPersistence.getWisdom(), legolasPersistence.getCharisma())
        );
        when(adapter.toPlayerCharacterDTO(legolasPersistence)).thenReturn(legolasDTO);

        mockMvc.perform(delete("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(characterName))
                .andExpect(jsonPath("$.dexterity").value(20));

        verify(characterRepository).findByName(characterName);
        verify(itemRepository).findByName(itemName);
        verify(characterRepository).save(legolasPersistence);
        assertEquals(0, legolasPersistence.getInventory().size());
        assertFalse(legolasPersistence.getInventory().contains(bowPersistence));
    }

    @Test
    void testRemoveItemFromInventory_itemNotInInventory() throws Exception {
        String characterName = "Gimli";
        String itemName = "Battle Axe";
        PlayerCharacterPersistenceDTO gimliPersistence = new PlayerCharacterPersistenceDTO(1L, characterName, 16,14,18,10,12,10);
        ItemPersistenceDTO axePersistence = new ItemPersistenceDTO(1L, itemName, "A heavy axe", 4.0);

        when(characterRepository.findByName(characterName)).thenReturn(Optional.of(gimliPersistence));
        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(axePersistence));

        PlayerCharacterDTO gimliDTO = new PlayerCharacterDTO(
            gimliPersistence.getName(), 
            new CharacterStatsDTO(gimliPersistence.getStrength(), gimliPersistence.getDexterity(), gimliPersistence.getConstitution(), gimliPersistence.getIntelligence(), gimliPersistence.getWisdom(), gimliPersistence.getCharisma())
        );
        when(adapter.toPlayerCharacterDTO(gimliPersistence)).thenReturn(gimliDTO);

        mockMvc.perform(delete("/characters/{characterName}/inventory/{itemName}", characterName, itemName))
                .andExpect(status().isBadRequest());

        verify(characterRepository).findByName(characterName);
        verify(itemRepository).findByName(itemName);
        verify(characterRepository, never()).save(any(PlayerCharacterPersistenceDTO.class));
    }
}
