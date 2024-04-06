package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@Service
public class OpenAIService {
    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-turbo-preview";
    // public static final String GPT4V = "gpt-4-vision-preview";

    private final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final OpenAIInterface openAIInterface;

    @Autowired
    public OpenAIService(OpenAIInterface openAIInterface) {
        this.openAIInterface = openAIInterface;
    }

    public List<String> getModelNames() {
        return openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .sorted()
                .toList();
    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        ChatRequest chatRequest = new ChatRequest(model, messages, temperature);
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info("Usage: {}", response.usage());
        return response.choices().getFirst().message().content();
    }

    public String getChatResponse(String message) {
        return getChatResponse(GPT4, List.of(new Message(Role.USER, message)), 0.7);
    }

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return createChatRequest(prompt);
    }

    private ChatRequest createChatRequest(String prompt) {
        return new ChatRequest(OpenAIService.GPT4,
                List.of(new Message(Role.USER, prompt)),
                0.7);
    }
}
