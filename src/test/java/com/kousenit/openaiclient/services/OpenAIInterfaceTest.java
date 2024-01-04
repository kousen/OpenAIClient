package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenAIInterfaceTest {
    private final Logger logger = LoggerFactory.getLogger(OpenAIInterfaceTest.class);

    @Autowired
    private OpenAIInterface openAIInterface;

    @Test
    void listModelIds() {
        openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .forEach(System.out::println);
    }

    @Test
    void listGPTModels() {
        openAIInterface.listModels().data().stream()
                .filter(model -> model.id().contains("gpt"))
                .forEach(System.out::println);
    }

    @Test
    void accessGPTChat() {
        ChatRequest chatRequest =
                openAIInterface.createChatRequest("Say this is a test!");
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info(response.usage().toString());
        assertThat(response.choices().getFirst().message().content())
                .isEqualTo("This is a test!");
    }

    @Test
    void extractStringResponse() {
        ChatRequest chatRequest =
                openAIInterface.createChatRequest("Say this is a test!");
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info(response.usage().toString());
        assertThat(openAIInterface.extractStringResponse(response))
                .isEqualTo("This is a test!");
    }

}