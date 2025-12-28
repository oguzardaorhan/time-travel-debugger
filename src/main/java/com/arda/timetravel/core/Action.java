package com.arda.timetravel.core;

import java.util.Map;

public class Action {

    private final String type;
    private final Map<String, Object> payload;

    public Action(String type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public static Action of(String type, Map<String, Object> payload) {
        return new Action(type, payload);
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
