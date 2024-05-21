package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kousenit.openaiclient.json.Role;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SerializerTests {
    @Autowired
    private ObjectMapper objectMapper;

    public sealed interface UnifiedMessage
            permits SimpleMessage, ComplexMessage {}
    public record SimpleMessage(Role role, String content) implements UnifiedMessage {}
    public record ComplexMessage(Role role, List<Content> content) implements UnifiedMessage {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TextContent.class, name = "text"),
            @JsonSubTypes.Type(value = ImageContent.class, name = "image_url")
    })
    public sealed interface Content permits TextContent, ImageContent {}
    public record TextContent(String text) implements Content {}
    public record ImageContent(ImageUrl image_url) implements Content {}
    public record ImageUrl(String url) {}

    public record ChatRequest(
            String model,
            List<UnifiedMessage> messages,
            double temperature,
            int max_tokens
    ) {}

    @BeforeEach
    void setUp() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    void chatRequest() throws JsonProcessingException, JSONException {
        // Simple message
        ChatRequest chatRequest = getChatRequest();

        String jsonString = objectMapper.writeValueAsString(chatRequest);
        System.out.println(jsonString);

        String expectedJson = """
            {
              "model": "gpt-4o",
              "messages": [
                {
                  "role": "user",
                  "content": "Hello, world!"
                },
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
              "temperature": 0.7,
              "max_tokens": 300
            }
        """;

        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }

    private static ChatRequest getChatRequest() {
        SimpleMessage simpleMessage = new SimpleMessage(Role.USER, "Hello, world!");

        // Complex message
        ImageUrl imageUrl = new ImageUrl("data:image/jpeg;base64,{base64_image}");
        Content imageContent = new ImageContent(imageUrl);
        Content textContent = new TextContent("What’s in this image?");
        ComplexMessage complexMessage = new ComplexMessage(Role.USER, List.of(textContent, imageContent));

        // ChatRequest
        return new ChatRequest(
                "gpt-4o",
                List.of(simpleMessage, complexMessage),
                0.7,
                300
        );
    }

    @Test
    void simpleMessage() throws JsonProcessingException, JSONException {
        SimpleMessage simpleMessage = new SimpleMessage(
                Role.USER, "Hello, world!");
        String jsonString = objectMapper.writeValueAsString(simpleMessage);
        System.out.println(jsonString);

        String expectedJson = """
            {
              "role": "user",
              "content": "Hello, world!"
            }
        """;

        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }

    @Test
    void complexMessage() throws JsonProcessingException, JSONException {
        ImageUrl imageUrl = new ImageUrl("data:image/jpeg;base64,{base64_image}");
        Content imageContent = new ImageContent(imageUrl);
        Content textContent = new TextContent("What’s in this image?");
        ComplexMessage complexMessage = new ComplexMessage(Role.USER, List.of(textContent, imageContent));

        String jsonString = objectMapper.writeValueAsString(complexMessage);
        System.out.println(jsonString);

        String expectedJson = """
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
                    "url": "data:image/jpeg;base64,{base64_image}"
                  }
                }
              ]
            }
        """;

        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }
}
