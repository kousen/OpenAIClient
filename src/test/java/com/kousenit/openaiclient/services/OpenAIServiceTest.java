package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ChatRequest;
import com.kousenit.openaiclient.json.ImageRequest;
import com.kousenit.openaiclient.json.Message;
import com.kousenit.openaiclient.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
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
        String response = openAIService.getChatResponse(OpenAIService.GPT4,
                List.of(new Message(Role.USER,
                        """
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe, and Everything?""")),
                0.2);
        System.out.println(response);
        assertTrue(response.contains("42"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void createChatRequestFromDefaults() {
        String prompt = """
                According to Douglas Adams, what is the Ultimate Answer
                to the Ultimate Question of Life, the Universe, and Everything?""";

        ChatRequest chatRequest = openAIService.createChatRequestFromDefaults(prompt);
        assertAll(
                () -> assertThat(chatRequest.model()).isEqualTo(OpenAIService.GPT35),
                () -> assertThat(chatRequest.temperature()).isEqualTo(0.7),
                () -> assertThat(chatRequest.messages()).isNotNull(),
                () -> assertThat(chatRequest.messages()).hasSize(1),
                () -> assertThat(chatRequest.messages().get(0).role()).isEqualTo(Role.USER),
                () -> assertThat(chatRequest.messages().get(0).content()).isEqualTo(prompt));
    }

    @Test
    void createImageRequestFromDefaults() {
        String prompt = """
                Portrait photography during the golden hour,
                using the soft, warm light to highlight the subject.""";
        ImageRequest imageRequest = openAIService.createImageRequestFromDefaults(prompt, 1, "512x512");
        assertAll(
                () -> assertThat(imageRequest.prompt()).isEqualTo(prompt),
                () -> assertThat(imageRequest.n()).isEqualTo(1),
                () -> assertThat(imageRequest.size()).isEqualTo("512x512"),
                () -> assertThat(imageRequest.response_format()).isEqualTo("b64_json"));
    }
}