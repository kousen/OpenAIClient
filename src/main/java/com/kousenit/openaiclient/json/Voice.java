package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Random;

public enum Voice {
    ALLOY, ECHO, FABLE, ONYX, NOVA, SHIMMER;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }

    private static final Voice[] VOICES = values();
    private static final int SIZE = VOICES.length;
    private static final Random RANDOM = new Random();

    public static Voice randomVoice()  {
        return VOICES[RANDOM.nextInt(SIZE)];
    }
}
