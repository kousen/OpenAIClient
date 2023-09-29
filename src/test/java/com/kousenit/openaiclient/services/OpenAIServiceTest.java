package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenAIServiceTest {
    @Autowired
    private OpenAIService openAIService;

    @Test
    void getModelNames() {
        List<String> modelNames = openAIService.getModelNames();
        assertThat(modelNames).anyMatch(name -> name.contains("gpt"));
        modelNames.stream()
                .filter(name -> name.contains("gpt"))
                .forEach(System.out::println);
    }

    @Test
    void getChatResponse() {
        String response = openAIService.getChatResponse("gpt-4",
                List.of(new Message("user",
                        """
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe, and Everything?""")),
                0.7);
        System.out.println(response);
        assertTrue(response.contains("42"));
    }
}