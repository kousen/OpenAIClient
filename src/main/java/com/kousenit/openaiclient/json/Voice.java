package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

public enum Voice {
    @JsonProperty("alloy") ALLOY,
    @JsonProperty("echo") ECHO,
    @JsonProperty("fable") FABLE,
    @JsonProperty("onyx") ONYX,
    @JsonProperty("nova") NOVA,
    @JsonProperty("shimmer") SHIMMER;

    private static final Voice[] VOICES = values();
    private static final int SIZE = VOICES.length;
    private static final Random RANDOM = new Random();

    public static Voice randomVoice()  {
        return VOICES[RANDOM.nextInt(SIZE)];
    }
}
