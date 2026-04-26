package model;

import java.util.List;

public record Order(List<Item> items) {
    public Order {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public List<Item> getItems() {
        return items;
    }
}

