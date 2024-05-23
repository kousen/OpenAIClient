package com.kousenit.ollamaclient.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateImageRequest;
import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateTextRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class OllamaServiceTest {
    @Autowired
    private OllamaService service;

    @Test
    void getModels() {
        var models = service.getModels();
        models.forEach(System.out::println);
    }


    @Test
    void chat() {
        var response = service.chat(OllamaModels.LLAMA3,
                "Why is the sky blue?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @Test
    void chatWithDefaultModel() {
        var response = service.chatWithDefaultModel("Why is the sky blue?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @Test
    void conversation() {
        var response = service.conversation("llama3",
                "Why is the sky blue?",
                "Because of Rayleigh scattering.",
                "How is that different from Mie scattering?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {OllamaModels.ORCA_MINI,
            OllamaModels.LLAMA3, OllamaModels.GEMMA})
    void generateWithText(String model) {
        var textRequest = new OllamaGenerateTextRequest(
                model,
                "Why is the sky blue?",
                false);
        var response = service.generate(textRequest);
        assertFalse(response.isBlank());
        assertThat(response).containsIgnoringCase("scattering");
        System.out.println(response);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            OllamaModels.LLAVA, OllamaModels.LLAVA_LLAMA3,
            OllamaModels.LLAVA_PHI3, OllamaModels.BAKLLAVA,
            OllamaModels.MOONDREAM})
    void describeImage(String model) {
        var imageRequest = new OllamaGenerateImageRequest(
                model,
                "What is in this image?",
                List.of("src/main/resources/images/cats_playing_cards.png"),
                false);
        var response = service.generate(imageRequest);
        assertFalse(response.isBlank());
        System.out.println(response);
    }

}