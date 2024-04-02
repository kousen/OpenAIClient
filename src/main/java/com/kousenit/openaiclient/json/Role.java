package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    USER, SYSTEM, ASSISTANT;

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Role fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}