package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ChatRequest;
import com.kousenit.openaiclient.json.ImageRequest;
import com.kousenit.openaiclient.json.Message;
import com.kousenit.openaiclient.json.Voice;
import com.kousenit.openaiclient.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                () -> assertThat(chatRequest.model()).isEqualTo(OpenAIService.GPT35),
                () -> assertThat(chatRequest.temperature()).isEqualTo(0.7),
                () -> assertThat(chatRequest.messages()).hasSize(1),
                () -> assertThat(chatRequest.messages()
                        .get(0)
                        .role()).isEqualTo(Role.USER),
                () -> assertThat(chatRequest.messages()
                        .get(0)
                        .content()).isEqualTo(prompt));
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

    @Test
    void getAudioResponse() {
        String prompt = """
                The YouTube channel, "Tales from the jar side" is your best
                source for learning about Java, Spring, and other open source
                technologies, especially when combined with AI tools.
                      
                The companion newsletter of the same name, hosted on Substack,
                is also a lot of fun.
                """;
        openAIService.getAudioResponse(prompt);
    }

    @Test
    void playMp3UsingJLayer() {
        openAIService.playMp3UsingJLayer("tftjs.mp3");
    }

    @Test
    void createAndPlay() {
        String prompt = """
                The YouTube channel, "Tales from the jar side" is your best
                source for learning about Java, Spring, and other open source
                technologies, especially when combined with AI tools.
                      
                The companion newsletter of the same name, hosted on Substack,
                is also a lot of fun.
                """;
        openAIService.createAndPlay(prompt, Voice.FABLE);
    }
}