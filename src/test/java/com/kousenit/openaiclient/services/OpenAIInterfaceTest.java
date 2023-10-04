package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import com.kousenit.openaiclient.util.FileUtils;
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
        assertThat(response.choices().get(0).message().content())
                .isEqualTo("This is a test!");
    }


    @Test
    void accessDallE() {
        ImageRequest imageRequest = openAIInterface.createImageRequest(
                """
                        Portrait photography during the golden hour,
                        using the soft, warm light to highlight the subject.
                        """, 1, "512x512");
        System.out.println(imageRequest);
        ImageResponse response = openAIInterface.getImageResponse(imageRequest);
        assertThat(response.data().size()).isEqualTo(1);
        FileUtils.writeImageToFile(response.data().get(0).b64_json());
    }
}