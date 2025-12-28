package com.arda.timetravel.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppState implements Serializable {

    private final List<String> items = new ArrayList<>();

    public List<String> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "AppState{items=" + items + '}';
    }
}
