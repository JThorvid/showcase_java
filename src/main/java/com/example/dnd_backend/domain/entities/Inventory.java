package com.example.dnd_backend.domain.entities;

import com.example.dnd_backend.domain.value_objects.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Inventory {
    private final Map<Item, Integer> countPerItem = new HashMap<>();

    public void add(Item item) {
        if (countPerItem.containsKey(item)) {
            countPerItem.put(item, countPerItem.get(item) + 1);
        } else {
            countPerItem.put(item, 1);
        }
    }

    public void remove(Item item) {
        if (!countPerItem.containsKey(item)) return;
        if (countPerItem.get(item) > 1) {
            countPerItem.put(item, countPerItem.get(item) - 1);
        } else {
            countPerItem.remove(item);
        }
    }
}
