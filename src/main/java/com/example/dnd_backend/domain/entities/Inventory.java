package com.example.dnd_backend.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory that)) return false;

        for (Map.Entry<String, Integer> entry : countPerItem.entrySet()) {
            if (!that.countPerItem.containsKey(entry.getKey())) return false;
            if (!Objects.equals(that.countPerItem.get(entry.getKey()), entry.getValue())) return false;
        }
        for (Map.Entry<String, Integer> entry : that.countPerItem.entrySet()) {
            if (!countPerItem.containsKey(entry.getKey())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(countPerItem);
    }
}
