package com.kousenit.stabilityai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.stabilityai.util.ImageResizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static com.kousenit.stabilityai.json.StabilityAiRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StabilityAiServiceTest {

    @Autowired
    private StabilityAiService stabilityAiService;

    @Test
    void requestStabilityAiImage() {
        byte[] bytes = stabilityAiService.requestStabilityAiImage("""
                cats playing gin rummy
                """);
        assertThat(bytes).isNotEmpty();
        System.out.println("bytes.length = " + bytes.length);
    }

    @Test
    void requestImageToVideo(@Value("classpath:images/resized_image.png") Resource image) {
        String videoId = stabilityAiService.requestImageToVideo(image);
        assertThat(videoId).isNotEmpty();
        System.out.println("videoId = " + videoId);
    }

    @Test
    void customDeserializer(@Autowired ObjectMapper objectMapper) throws Exception {
        File file = new File("src/main/resources/text/response_20240418183755.txt");
        VideoResponse video = objectMapper.readValue(file, VideoResponse.class);
        switch (video) {
            case VideoCompleted completed -> System.out.println("completed.finish_reason() = " + completed.finish_reason());
            case VideoInProgress progress -> System.out.println("progress = " + progress);
            case VideoErrors errors -> System.out.println("errors = " + errors);
        }
        assertThat(video).isInstanceOf(VideoCompleted.class);
    }

    @Test
    void generateAndDeserializeVideo() throws IOException {
        // generate image
        byte[] bytes = stabilityAiService.requestStabilityAiImage("dogs playing poker");
        assertThat(bytes).isNotEmpty();

        // resize image
        Resource resource = ImageResizer.resizeImageAsResource(bytes, 768, 768);

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