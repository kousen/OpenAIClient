package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ChatResponse;
import com.kousenit.openaiclient.json.Message;
import com.kousenit.openaiclient.json.ModelList;
import com.kousenit.openaiclient.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MistralServiceTest {
    @Autowired
    private MistralService service;

    @Test
    void complete() {
        Message message = new Message(Role.USER,
                "Who is the most renowed French painter?");
        String model = MistralService.MISTRAL_SMALL_LATEST;
        ChatResponse response = service.complete(model, List.of(message));
        assertNotNull(response);
        System.out.println(response);
        assertEquals("mistral-small-latest", response.model());
        System.out.println(response.choices().getFirst().message().content());
    }

    @Test
    void listModels() {
        List<String> models = service.listModels().data().stream()
                .peek(System.out::println)
                .map(ModelList.Model::id)
                .toList();
        assertNotNull(models);
        assertThat(models).contains(
                MistralService.MISTRAL_SMALL_LATEST,
                MistralService.MISTRAL_MEDIUM_LATEST,
                MistralService.MISTRAL_LARGE_LATEST,
                MistralService.OPEN_MISTRAL_7B,
                MistralService.OPEN_MIXTRAL_8x7B);
    }
}