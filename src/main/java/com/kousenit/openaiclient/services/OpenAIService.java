package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.json.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@Service
public class OpenAIService {
    private final ObjectMapper objectMapper;

    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-turbo-preview";
    //public static final String GPT4V = "gpt-4-vision-preview";

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
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer %s".formatted(System.getenv("OPENAI_API_KEY")))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
        ResponseEntity<ChatResponse> entity = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", request, ChatResponse.class);
        // return openAIInterface.getChatResponse(request);
        return entity.getBody();

    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        ChatRequest chatRequest = new ChatRequest(model, MAX_TOKENS, temperature, messages);
        System.out.println(chatRequest);
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
