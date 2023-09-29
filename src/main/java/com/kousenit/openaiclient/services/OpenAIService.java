package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ChatRequest;
import com.kousenit.openaiclient.json.ChatResponse;
import com.kousenit.openaiclient.json.Message;
import com.kousenit.openaiclient.json.ModelList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIService {
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
}
