package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseFormat {
    MP3, OPUS, AAC, FLAC, WAV, PCM;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
