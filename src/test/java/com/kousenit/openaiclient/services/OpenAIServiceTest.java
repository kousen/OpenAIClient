package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Role;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.ChatRequest;
import static com.kousenit.openaiclient.json.OpenAIRecords.Message;
import static com.kousenit.openaiclient.json.OpenAIRecords.ModelList.Model;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIServiceTest {
    @Autowired
    private OpenAIService openAIService;

    @Test
    void getModels() {
        List<Model> models = openAIService.getModels();
        models.forEach(System.out::println);
    }

    @Test
    void getGPTModelNames() {
        List<String> modelNames = openAIService.getModelNames();
        assertThat(modelNames)
                .anyMatch(name -> name.contains(OpenAIService.GPT35))
                .anyMatch(name -> name.contains(OpenAIService.GPT4))
                .anyMatch(name -> name.contains(OpenAIService.GPT4O));
        modelNames.stream()
                .filter(name -> name.contains("gpt"))
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void getAllModels() {
        openAIService.getModelNames().stream()
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void getChatResponse() {
        String response = openAIService.getChatResponse(OpenAIService.GPT4O_MINI,
                List.of(new Message(Role.USER,
                        """
                                According to Douglas Adams, what is the Ultimate Answer
                                to the Ultimate Question of Life, the Universe, and Everything?""")),
                0.2);
        System.out.println(response);
        assertTrue(response.contains("42"));
    }

    @Test
    void howManyRoads() {
        String response = openAIService.getChatResponse(OpenAIService.GPT4,
                List.of(new Message(Role.USER,
                        """
                                How many roads must a man walk down
                                before we call him a man?""")),
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
                () -> assertThat(chatRequest.messages().getFirst().content()).isEqualTo(prompt));
    }

    @Test
    void getChatResponseWithMessage() {
        String response = openAIService.getChatResponse(
                """
                    According to Douglas Adams, what is the Ultimate Answer
                    to the Ultimate Question of Life, the Universe,
                    and Everything?""");
        System.out.println(response);
        assertTrue(response.contains("42"));
    }

    @Test
    void transcribeAudioFromResource(
            @Value("classpath:audio/AssertJExceptions.wav") Resource wavFile) {
        String response = openAIService.getTranscription(wavFile);
        assertNotNull(response);
        assertThat(response).contains("AssertJ");
        System.out.println(response);
    }

    @Test
    void transcribeAudioFromLongerFile(@Value("classpath:audio/EarningsCall.wav") Resource wavFile) {
        String transcription = openAIService.getTranscription(wavFile);
        assertThat(transcription)
                .isNotBlank()
                .hasLineCount(3);
    }

    @Nested
    class TranslationsTests {
        @Test
        void translateAudioFromResource_chinese(
                @Value("classpath:audio/Kousen 录音.m4a") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }

        @Test
        void translateAudioFromResource_italian(
                @Value("classpath:audio/csw14_17_leciaramelle_64kb.mp3") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }

        @Test
        void translateAudioFromResource_swedish(
                @Value("classpath:audio/csw14_08_tomten_64kb.mp3") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }

        @Test
        void translateAudioFromResource_bulgarian(
                @Value("classpath:audio/csw14_27_koledendar_64kb.mp3") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }

        @Test
        void translateAudioFromResource_french(
                @Value("classpath:audio/csw14_23_lacharlotte_64kb.mp3") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }

        @Test
        void translateAudioFromResource_spanish(
                @Value("classpath:audio/Paraguay.ogg") Resource wavFile) {
            String response = openAIService.getTranslation(wavFile);
            assertNotNull(response);
            System.out.println(response);
        }
    }

}