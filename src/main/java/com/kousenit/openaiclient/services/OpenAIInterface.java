package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1")
public interface OpenAIInterface {

    // Models
    @GetExchange(value = "/models", accept = "application/json")
    ModelList listModels();

    // Chat
    @PostExchange(value = "/chat/completions",
            accept = "application/json", contentType = "application/json")
    ChatResponse getChatResponse(@RequestBody ChatRequest chatRequest);

    // Images
    @PostExchange(value = "/images/generations",
            accept = "application/json", contentType = "application/json")
    ImageResponse getImageResponse(@RequestBody ImageRequest imageRequest);

    // Text-to-Speech
    @PostExchange(value = "/audio/speech",
            accept = "audio/mpeg", contentType = "application/json")
    byte[] getTextToSpeechResponse(@RequestBody TTSRequest ttsRequest);

    // NOTE: See TranscriptService for the transcribing audio to text
}
