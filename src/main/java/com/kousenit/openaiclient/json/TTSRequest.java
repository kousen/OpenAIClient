package com.kousenit.openaiclient.json;

// voice: one of alloy, echo, fable, onyx, nova, and shimmer
// response_format: mp3, opus, aac, and flac
// speed: between 0.25 and 4.0
public record TTSRequest(
        String model,
        String input,
        Voice voice,
        ResponseFormat responseFormat,
        double speed
) {
    public TTSRequest(String model, String input, Voice voice) {
        this(model, input, voice, ResponseFormat.MP3, 1.0);
    }
}
