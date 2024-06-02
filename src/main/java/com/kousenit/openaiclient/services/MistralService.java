package com.kousenit.openaiclient.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@Service
public class MistralService {
    public static final String MISTRAL_SMALL_LATEST = "mistral-small-latest";
    public static final String MISTRAL_MEDIUM_LATEST = "mistral-medium-latest";
    public static final String MISTRAL_LARGE_LATEST = "mistral-large-latest";
    public static final String OPEN_MISTRAL_7B = "open-mistral-7b";
    public static final String OPEN_MIXTRAL_8x7B = "open-mixtral-8x7b";

    private final RestClient restClient;
    private final String apiKey;

    public MistralService(@Value("${mistral.api.key}") String apiKey,
                          @Value("${mistral.baseurl}") String baseUrl) {
        restClient = RestClient.create(baseUrl);
        this.apiKey = apiKey;
    }

    public ModelList listModels() {
        return restClient.get()
                .uri("/v1/models")
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .retrieve()
                .body(ModelList.class);
    }

    public ChatResponse complete(String model, List<Message> messages) {
        ChatRequest request = new ChatRequest(model, messages, 0.7);

        return restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);
    }
}
