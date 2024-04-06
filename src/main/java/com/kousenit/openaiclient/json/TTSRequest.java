package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record TTSRequest(
        @Pattern(regexp = "tts-1(-hd)?(-1106)?") String model,
        @NotBlank @Size(max = 4096) String input,
        Voice voice,
        @JsonProperty("response_format") ResponseFormat responseFormat,
        @DecimalMin("0.25") @DecimalMax("4.0") double speed
) {
    public TTSRequest {
        if (model == null) {
            model = "tts-1";
        }
        if (responseFormat == null) {
            responseFormat = ResponseFormat.MP3;
        }
        if (speed < 0.25 || speed > 4.0) {
            throw new IllegalArgumentException("Speed must be between 0.25 and 4.0");
        }
    }

    public TTSRequest(String model, String input, Voice voice) {
        this(model, input, voice, ResponseFormat.MP3, 1.0);
    }
}
