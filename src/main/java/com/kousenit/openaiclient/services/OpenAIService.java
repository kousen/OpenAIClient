package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ChatRequest;
import com.kousenit.openaiclient.json.ChatResponse;
import com.kousenit.openaiclient.json.Message;
import com.kousenit.openaiclient.json.ModelList;
import com.kousenit.openaiclient.util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIService {
    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-1106-preview";

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

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return createChatRequest(prompt);
    }

    private ChatRequest createChatRequest(String prompt) {
        return new ChatRequest(OpenAIService.GPT4,
                List.of(new Message(Role.USER, prompt)),
                0.7);
    }
}
