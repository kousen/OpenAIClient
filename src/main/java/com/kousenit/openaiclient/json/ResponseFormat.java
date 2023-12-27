package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseFormat {
    @JsonProperty("mp3")
    MP3,
    @JsonProperty("opus")
    OPUS,
    @JsonProperty("aac")
    AAC,
    @JsonProperty("flac")
    FLAC
}
