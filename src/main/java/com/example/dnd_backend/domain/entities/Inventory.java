package com.example.dnd_backend.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Inventory {
    private final Map<String, Integer> countPerItem = new HashMap<>();

    public void add(String itemName, int quantity) {
        if (countPerItem.containsKey(itemName)) {
            countPerItem.put(itemName, countPerItem.get(itemName) + quantity);
        } else {
            countPerItem.put(itemName, quantity);
        }
    }

    public void remove(String itemName, int quantity) {
        if (!countPerItem.containsKey(itemName)) return;
        if (countPerItem.get(itemName) > quantity) {
            countPerItem.put(itemName, countPerItem.get(itemName) - quantity);
        } else {
            countPerItem.remove(itemName);
        }
    }
}
