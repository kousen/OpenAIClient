package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Voice {
    @JsonProperty("alloy")
    ALLOY,
    @JsonProperty("echo")
    ECHO,
    @JsonProperty("fable")
    FABLE,
    @JsonProperty("onyx")
    ONYX,
    @JsonProperty("nova")
    NOVA,
    @JsonProperty("shimmer")
    SHIMMER
}
