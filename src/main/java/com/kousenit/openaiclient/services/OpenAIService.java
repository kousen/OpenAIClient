package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.json.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@Service
public class OpenAIService {
    private final ObjectMapper objectMapper;

    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-turbo-preview";
    public static final String GPT4V = "gpt-4-vision-preview";

    private static final int MAX_TOKENS = 300;

    private final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final OpenAIInterface openAIInterface;

    @Autowired
    public OpenAIService(OpenAIInterface openAIInterface, ObjectMapper objectMapper) {
        this.openAIInterface = openAIInterface;
        this.objectMapper = objectMapper;
    }

    public List<String> getModelNames() {
        return openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .sorted()
                .toList();
    }

    private void logJson(Object object) {
        try {
            logger.info(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatResponse getChatResponse(ChatRequest request) {
        logJson(request);
        return openAIInterface.getChatResponse(request);
    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        ChatRequest chatRequest = new ChatRequest(model, MAX_TOKENS, temperature, messages);
        logJson(chatRequest);
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        assert response != null;
        logger.info("Usage: {}", response.usage());
        return response.choices().getFirst().message().content();
    }

    public String getChatResponse(String message) {
        return getChatResponse(GPT4, List.of(
                new Message(Role.USER,
                        new SimpleTextContent(message.replaceAll("\n", " ")))),
                0.7);
    }

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return new ChatRequest(OpenAIService.GPT4,
                MAX_TOKENS,
                0.7,
                List.of(new Message(Role.USER,
                        new SimpleTextContent(prompt.replaceAll("\n", " ")))));
    }
}
