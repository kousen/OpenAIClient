package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import com.kousenit.openaiclient.util.Role;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/v1")
public interface OpenAIInterface {

    @GetExchange(value = "/models", accept = "application/json")
    ModelList listModels();

    @PostExchange(value = "/chat/completions", accept = "application/json", contentType = "application/json")
    ChatResponse getChatResponse(@RequestBody ChatRequest chatRequest);

    @PostExchange(value = "/images/generations", accept = "application/json", contentType = "application/json")
    ImageResponse getImageResponse(@RequestBody ImageRequest imageRequest);

    @PostExchange(value = "/audio/speech", accept = "audio/mpeg", contentType = "application/json")
    byte[] getTextToSpeechResponse(@RequestBody TTSRequest ttsRequest);

    default ChatRequest createChatRequest(String prompt) {
        return new ChatRequest(OpenAIService.GPT4,
                List.of(new Message(Role.USER, prompt)),
                0.7);
    }

    default ImageRequest createImageRequest(String prompt, int n, String size) {
        return new ImageRequest(prompt, n, size, "b64_json");
    }

    default String extractStringResponse(ChatResponse response) {
        return response.choices().get(0).message().content();
    }

}
