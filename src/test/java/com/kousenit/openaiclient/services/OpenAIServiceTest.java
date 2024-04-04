package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIServiceTest {
    @Autowired
    private OpenAIService openAIService;

    @Test
    void getGPTModels() {
        List<String> modelNames = openAIService.getModelNames();
        assertThat(modelNames)
                .anyMatch(name -> name.contains(OpenAIService.GPT35))
                .anyMatch(name -> name.contains(OpenAIService.GPT4));
        modelNames.stream()
                .filter(name -> name.contains("gpt"))
                .forEach(System.out::println);
    }

    @Test
    void getAllModels() {
        openAIService.getModelNames()
                .forEach(System.out::println);
    }

    @Test
    void getChatResponse() {
        String response = openAIService.getChatResponse(
                OpenAIService.GPT4,
                List.of(new Message(Role.USER,
                        new SimpleTextContent("""
                                According to Douglas Adams, what is the Ultimate Answer
                                to the Ultimate Question of Life, the Universe, and Everything?"""))),
                0.2);
        System.out.println(response);
        assertTrue(response.contains("42"));
    }

    @Test
    void getChatResponseWithRequest() {
        ChatRequest chatRequest = new ChatRequest(OpenAIService.GPT4, 100, 0.2,
                List.of(new Message(Role.USER,
//                        new ComplexContent(List.of(
//                                new Text("text", """
//                                        According to Douglas Adams, what is the Ultimate Answer
//                                        to the Ultimate Question of Life, the Universe, and Everything?"""
//                                        .replaceAll("\n", " ")))))));
                        new SimpleTextContent("""
                                What is the Ultimate Answer to the Ultimate Question
                                of Life, the Universe, and Everything?"""
                                .replaceAll("\n", " ")))));
        System.out.println(chatRequest);
        ChatResponse response = openAIService.getChatResponse(chatRequest);
        System.out.println(response);
    }

    @Test
    void howManyRoads() {
        String response = openAIService.getChatResponse(OpenAIService.GPT4,
                List.of(new Message(Role.USER,
                        new SimpleTextContent("""
                                How many roads must a man walk down
                                before we call him a man?"""))),
                0.2);
        System.out.println(response);
        assertNotNull(response);
    }

    @Test
    void createChatRequestFromDefaults() {
        String prompt = """
                According to Douglas Adams, what is the Ultimate Answer
                to the Ultimate Question of Life, the Universe, and Everything?""";

        ChatRequest chatRequest = openAIService.createChatRequestFromDefaults(prompt);
        assertThat(chatRequest.messages()).isNotNull();
        assertAll(
                () -> assertThat(chatRequest.model()).isEqualTo(OpenAIService.GPT4),
                () -> assertThat(chatRequest.temperature()).isEqualTo(0.7),
                () -> assertThat(chatRequest.messages()).hasSize(1),
                () -> assertThat(chatRequest.messages().getFirst().role()).isEqualTo(Role.USER),
                () -> assertThat(chatRequest.messages().getFirst().content()).isEqualTo(
                        new SimpleTextContent(prompt)));
    }

    @Test
    void getChatResponseWithMessage() {
        String response = openAIService.getChatResponse(
                """
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe,
                        and Everything?""");
        System.out.println("Response: " + response);
        assertTrue(response.contains("42"));
    }
}