package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.json.OpenAIRecords;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.openaiclient.services.VisionService.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VisionServiceTest {
    @Autowired
    private VisionService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getVisionResponseV1() {
        String response = service.getVisionResponseV1(
                "src/main/resources/images/cats_playing_cards.png");
        System.out.println(response);
        assertTrue(response.contains("cat"));
    }

    @Test
    void getVisionResponseV2() {
        String response = service.getVisionResponseV2(
                "src/main/resources/images/cats_playing_cards.png");
        System.out.println(response);
        assertTrue(response.contains("cat"));
    }

    @Test
    void getVisionResponseV3() {
        OpenAIRecords.ChatResponse response = service.getVisionResponseV3(
                "src/main/resources/images/cats_playing_cards.png");
        System.out.println(response);
        assertTrue(response.toString().contains("cat"));
    }

    @Test
    void generateJSONTest() throws Exception {
        ImageUrl imageUrl = new ImageUrl("data:image/jpeg;base64,{base64_image}");
        Content imageContent = new ImageContent(imageUrl);
        Content textContent = new TextContent("What’s in this image?");
        Message userMessage = new Message("user", List.of(textContent, imageContent));
        ChatRequest chatRequest = new ChatRequest("gpt-4o", List.of(userMessage), 300);

        String actualJson = objectMapper.writeValueAsString(chatRequest);
        String expectedJson = """
            {
              "model": "gpt-4o",
              "messages": [
                {
                  "role": "user",
                  "content": [
                    {
                      "type": "text",
                      "text": "What’s in this image?"
                    },
                    {
                      "type": "image_url",
                      "image_url": {
                        "url": "data:image/png;base64,{base64_image}"
                      }
                    }
                  ]
                }
              ],
              "max_tokens": 300
            }
            """;

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

}