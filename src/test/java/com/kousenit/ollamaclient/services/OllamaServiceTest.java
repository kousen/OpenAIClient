package com.kousenit.ollamaclient.services;

import com.kousenit.ollamaclient.config.OllamaConfig;
import com.kousenit.ollamaclient.json.OllamaRecords;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateImageRequest;
import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateTextRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

//@SpringBootTest
@WebFluxTest(OllamaService.class)
@Import(OllamaConfig.class)
class OllamaServiceTest {
    @Autowired
    private OllamaService service;

    @Test
    void getModels() {
        var models = service.getModels();
        models.stream()
                .map(OllamaRecords.ModelList.OllamaModel::name)
                .sorted()
                .forEach(System.out::println);
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

    @Test
    void conversation1() {
        var response = service.conversation("llama3",
                """
                I am the most successful author on the Pragmatic Bookshelf.
                Who am I?""",
                "Venkat Subramaniam",
                "Okay, other than him. Now who am I?",
                "Bruce Tate? Johanna Rothman? Dave Thomas?",
                "Cripes, never mind.");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {OllamaModels.ORCA_MINI,
            OllamaModels.LLAMA3, OllamaModels.GEMMA2})
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
                "Please give me an inventory of the books on this shelf.",
                List.of("src/main/resources/images/books_on_shelf.jpg"),
                false);
        var response = service.generate(imageRequest);
        assertFalse(response.isBlank());
        System.out.println(response);
    }

//    void asyncChat() {
//        var response = service.asyncChat(OllamaModels.ORCA_MINI,
//                "Why is the sky blue?");
//
//        StepVerifier.create(response)
//                .consumeNextWith(resp -> {
//                    System.out.println("Response: " + resp);
//                    assert resp.contains("scattering"); // Adjust the expected response as needed
//                })
//                .verifyComplete();
//    }
}