package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIService {
    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-3.4";

    private final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final List<String> modelNames = new ArrayList<>();

    private final OpenAIInterface openAIInterface;

    @Autowired
    public OpenAIService(OpenAIInterface openAIInterface) {
        this.openAIInterface = openAIInterface;

        modelNames.addAll(getModelNames());
    }

    public List<String> getModelNames() {
        return openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .toList();
    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        if (!modelNames.contains(model)) {
            throw new IllegalArgumentException("Invalid model name: " + model);
        }
        ChatRequest chatRequest = new ChatRequest(model, messages, temperature);
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info("Usage: {}", response.usage());
        return response.choices().get(0).message().content();
    }

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return openAIInterface.createChatRequest(prompt);
    }

    public ImageRequest createImageRequestFromDefaults(String prompt, int n, String size) {
        return openAIInterface.createImageRequest(prompt, n, size);
    }
}
