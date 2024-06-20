package com.kousenit.stabilityai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.util.FileUtils;
import com.kousenit.stabilityai.util.ImageResizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.kousenit.stabilityai.json.StabilityAiRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StabilityAiServiceTest {

    @Autowired
    private StabilityAiService stabilityAiService;

    @ParameterizedTest(name = "Request stability AI image with model: {0}")
    @ValueSource(strings = {"sd3-medium", "sd3-large", "sd3-large-turbo"})
    void requestStabilityAiImage(String model) {
        byte[] bytes = stabilityAiService.requestStabilityAiImage("""
                stochastic parrots playing chess
                """,
                model);
        assertThat(bytes).isNotEmpty();
        System.out.println("bytes.length = " + bytes.length);
    }

    @Test
    void requestStabilityAiImageUltra() {
        byte[] bytes = stabilityAiService.requestStabilityAiImageUltra("""
                cats playing gin rummy
                """);
        assertThat(bytes).isNotEmpty();
        System.out.println("bytes.length = " + bytes.length);
    }

    @Test
    void requestImageToVideo(@Value("classpath:images/resized_image.png") Resource image) {
        String videoId = stabilityAiService.requestImageToVideo(image);
        assertThat(videoId)
                .isNotEmpty()
                .hasSize(64);
        System.out.println("videoId = " + videoId);

        // poll video status every 10 sec, saving to a file when complete
        stabilityAiService.checkVideoStatus(videoId);
    }

    @Test
    void checkVideoStatus() {
        stabilityAiService.checkVideoStatus("b345396809e92fa38074d3644ad1f4a614701571afa33c95da635299a876ab47");
    }

    @Test
    void customDeserializer(@Autowired ObjectMapper objectMapper) throws Exception {
        File file = new File("src/main/resources/text/response_20240419133252.txt");
        VideoResponse video = objectMapper.readValue(file, VideoResponse.class);
        switch (video) {
            case VideoCompleted completed -> {
                System.out.println("completed.finish_reason() = " + completed.finish_reason());
                // write the video() property to a file
                FileUtils.writeVideoBytesToFile(completed.video());
            }
            case VideoInProgress progress -> System.out.println("progress = " + progress);
            case VideoErrors errors -> System.out.println("errors = " + errors);
        }
        assertThat(video).isInstanceOf(VideoCompleted.class);
    }

    @Test
    void resizeAndGenerateVideo(@Value("classpath:images/image_20240419092834_0.png") Resource image) throws IOException {
//        // generate image
//        byte[] bytes = stabilityAiService.requestStabilityAiImage("cats playing canasta");
//        assertThat(bytes).isNotEmpty();

        // load image as byte array
        byte[] bytes;
        try (InputStream imgStream = image.getInputStream()) {
            bytes = imgStream.readAllBytes();
        }

        // resize image
        Resource resource = ImageResizer.resizeImageAsResource(bytes, 768, 768);
        assertThat(resource)
                .isNotNull()
                .satisfies(r -> assertThat(r.exists()).isTrue())
                .satisfies(r -> assertThat(r.contentLength()).isGreaterThan(0));

        // request video, get back id
        String videoId = stabilityAiService.requestImageToVideo(resource);
        assertThat(videoId)
                .isNotEmpty()
                .hasSize(64);
        System.out.println("videoId = " + videoId);

        // poll video status every 10 sec, saving to a file when complete
        stabilityAiService.checkVideoStatus(videoId);
    }
}