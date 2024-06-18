package com.kousenit.stabilityai.services;

import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.kousenit.stabilityai.json.StabilityAiRecords.*;

@Service
public class StabilityAiService {
    private static final Logger logger = LoggerFactory.getLogger(StabilityAiService.class);

    private final StabilityAiInterface stabilityAiInterface;
    private final ThreadPoolTaskScheduler taskScheduler;

    public StabilityAiService(StabilityAiInterface stabilityAiInterface, ThreadPoolTaskScheduler taskScheduler) {
        this.stabilityAiInterface = stabilityAiInterface;
        this.taskScheduler = taskScheduler;
    }

    public byte[] requestStabilityAiImage(String prompt, String model) {
        logger.info("Requesting {} image for prompt: {}", model, prompt);
        byte[] bytes = stabilityAiInterface.requestStableImage(
                prompt, model, "1:1", "png");
        boolean success = FileUtils.writeImageBytesToFile(bytes);
        if (!success) {
            throw new RuntimeException("Failed to write image to file");
        }
        return bytes;
    }

    public byte[] requestStabilityAiImageUltra(String prompt) {
        logger.info("Requesting ultra image for prompt: {}", prompt);
        byte[] bytes = stabilityAiInterface.requestStableImageUltra(
                prompt, "1:1", "png");
        boolean success = FileUtils.writeImageBytesToFile(bytes);
        if (!success) {
            throw new RuntimeException("Failed to write image to file");
        }
        return bytes;
    }

    public String requestImageToVideo(Resource image) {
        return stabilityAiInterface.requestImageToVideo(
                image, 1.8, 127).id();
    }

    public void checkVideoStatus(String videoId) {
        ResponseEntity<VideoResponse> videoStatusResponse =
                stabilityAiInterface.getVideoStatus(videoId);
        if (videoStatusResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Video status: {}", videoStatusResponse.getBody());
            VideoResponse videoResponse = videoStatusResponse.getBody();
            if (videoResponse != null) {
                switch (videoResponse) {
                    case VideoCompleted completed -> {
                        logger.info("Finish reason: {}", completed.finish_reason());
                        FileUtils.writeVideoBytesToFile(completed.video());
                    }
                    case VideoInProgress progress -> {
                        logger.info("Status: {}. Checking again in 10 seconds.", progress.status());
                        taskScheduler.schedule(() -> checkVideoStatus(videoId),
                                Instant.now().plusSeconds(10));
                    }
                    case VideoErrors errors -> {
                        logger.error("Error fetching video status: {}", errors.name());
                        errors.errors().forEach(System.out::println);
                    }
                }
            } else {
                logger.warn("Failed to retrieve video status.");
            }
        } else {
            logger.error("Failed to retrieve video status: {}", videoStatusResponse.getStatusCode());
        }
    }

}
