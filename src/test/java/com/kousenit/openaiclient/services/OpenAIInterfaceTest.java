package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenAIInterfaceTest {
    private final Logger logger = LoggerFactory.getLogger(OpenAIInterfaceTest.class);

    @Autowired
    private OpenAIInterface openAIInterface;

    public ChatRequest createChatRequest(String prompt) {
        return new ChatRequest("gpt-3.5-turbo",
                List.of(new Message("user", prompt)),
                0.7);
    }

    public ImageRequest createImageRequest(String prompt, int n, String size) {
        return new ImageRequest(prompt, n, size, "b64_json");
    }

    @Test
    void listModels() {
        openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .filter(id -> id.contains("gpt"))
                .forEach(System.out::println);
    }

    @Test
    void accessGPTChat() {
        ChatRequest chatRequest = createChatRequest("Say this is a test!");
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info(response.usage().toString());
        assertThat(response.choices().get(0).message().content()).isEqualTo("This is a test!");
    }

    @Test
    void accessDallE() {
        ImageRequest imageRequest = createImageRequest(
                """
                        A photo of a penguin wearing a Batman suit
                        fighting crime in the Antarctic
                        """, 1, "512x512");
        System.out.println(imageRequest);
        ImageResponse response = openAIInterface.getImageResponse(imageRequest);
        assertThat(response.data().size()).isEqualTo(1);
    }
}