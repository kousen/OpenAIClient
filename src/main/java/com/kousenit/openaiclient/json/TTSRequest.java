package com.kousenit.openaiclient.json;

import jakarta.validation.constraints.*;

public record TTSRequest(
        @Pattern(regexp = "tts-1(-hd)?") String model,
        @NotBlank @Size(max = 4096) String input,
        Voice voice,
        ResponseFormat responseFormat,
        @DecimalMin("0.25") @DecimalMax("4.0") double speed
) {
    public TTSRequest(String model, String input, Voice voice) {
        this(model, input, voice, ResponseFormat.MP3, 1.0);
    }
}
