package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Random;

public enum Voice {
    ALLOY, ECHO, FABLE, ONYX, NOVA, SHIMMER;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }

    public static Voice randomVoice()  {
        Random random = new Random();
        Voice[] voices = values();
        return voices[random.nextInt(voices.length)];
    }
}
