package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.json.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@SpringBootTest
class OpenAIInterfaceTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenAIInterface openAIInterface;

    @Test
    void listModelIds() {
        openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void listGPTModels() {
        openAIInterface.listModels().data().stream()
                .filter(model -> model.id().contains("gpt"))
                .forEach(System.out::println);
    }

    @Test
    void chatWithGPT4() throws JsonProcessingException {
        var request = new ChatRequest("gpt-4-turbo-preview", 200, 0.7,
                List.of(new Message(Role.USER,
                        new SimpleTextContent("What is the best way to cook a steak?"))));
        System.out.println("Request: " + request);
        System.out.println("JSON:    " + objectMapper.writeValueAsString(request));
        try {
            var response = openAIInterface.getChatResponse(request);
            System.out.println(response);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}