package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ImageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class DallEServiceTest {
    @Autowired
    private DallEService dallEService;

    private static final String DEFAULT_IMAGE_PROMPT = """
            A photorealistic image of a happy robot
            leaping into the air in joy
            after accomplishing a hard task.
            The robot is surrounded by many green plants
            emphasizing the growth in the Spring.
            """;

    @Test
    void downloadDallE2ImagesFromPromptAndNumber() {
        dallEService.downloadImagesFromPromptAndNumber(
                DallEService.DALL_E_2, DEFAULT_IMAGE_PROMPT, 4);
    }

    @Test
    void downloadDallE3ImagesFromPromptAndNumber() {
        dallEService.downloadImagesFromPromptAndNumber(
                DallEService.DALL_E_3, DEFAULT_IMAGE_PROMPT, 1);
    }

    @Test
    void createImageRequestFromDefaults() {
        ImageRequest imageRequest = dallEService.createImageRequestFromDefaults(DEFAULT_IMAGE_PROMPT, 1);
        assertAll(
                () -> assertThat(imageRequest.model()).isEqualTo(DallEService.DALL_E_3),
                () -> assertThat(imageRequest.prompt()).isEqualTo(DEFAULT_IMAGE_PROMPT),
                () -> assertThat(imageRequest.n()).isOne(),
                () -> assertThat(imageRequest.size()).isEqualTo("1024x1024"),
                () -> assertThat(imageRequest.responseFormat()).isEqualTo("b64_json"));
    }

}