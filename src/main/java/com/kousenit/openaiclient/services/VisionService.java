package com.kousenit.openaiclient.services;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kousenit.ollamaclient.utils.FileUtils;
import com.kousenit.openaiclient.json.OpenAIRecords;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class VisionService {

    private final WebClient openAIWebClient;
    private final OpenAIInterface openAIInterface;

    // Autowire in the RestClient defined in OpenAiConfig
    public VisionService(WebClient openAIWebClient, OpenAIInterface openAIInterface) {
        this.openAIWebClient = openAIWebClient;
        this.openAIInterface = openAIInterface;
    }

    public record ChatRequest(
            String model,
            List<Message> messages,
            int max_tokens
    ) {}

    public record Message(String role, List<Content> content) {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TextContent.class, name = "text"),
            @JsonSubTypes.Type(value = ImageContent.class, name = "image_url")
    })
    public sealed interface Content permits TextContent, ImageContent {}
    public record TextContent(String text) implements Content {}
    public record ImageContent(ImageUrl image_url) implements Content {}
    public record ImageUrl(String url) {}

    public OpenAIRecords.ChatResponse getVisionResponseV3(String fileName) {
        String encodeImage = FileUtils.encodeImage(fileName);
        ImageUrl imageUrl = new ImageUrl("data:image/jpeg;base64,%s".formatted(encodeImage));
        Content imageContent = new ImageContent(imageUrl);
        Content textContent = new TextContent("What’s in this image?");
        Message userMessage = new Message("user", List.of(textContent, imageContent));
        ChatRequest chatRequest = new ChatRequest("gpt-4o", List.of(userMessage), 500);
        return openAIInterface.getVisionResponse(chatRequest);
    }

    public String getVisionResponseV2(String fileName) {
        //language=JSON
        String payload = """
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
                            "url": "data:image/png;base64,%s"
                          }
                        }
                      ]
                    }
                  ],
                  "max_tokens": 300
                }
                """;

        payload = payload.formatted(FileUtils.encodeImage(fileName));
        System.out.println("Payload size: " + payload.length());

        return openAIWebClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getVisionResponseV1(String fileName) {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer %s"
                        .formatted(System.getenv("OPENAI_API_KEY")))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        String payload = """
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
                            "url": "data:image/png;base64,%s"
                          }
                        }
                      ]
                    }
                  ],
                  "max_tokens": 300
                }
                """;

        return client.post()
                .uri("/chat/completions")
                .body(payload.formatted(FileUtils.encodeImage(fileName)))
                .retrieve()
                .body(String.class);
    }
}
