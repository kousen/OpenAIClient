package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Image;
import com.kousenit.openaiclient.json.ImageRequest;
import com.kousenit.openaiclient.json.ImageResponse;
import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DallEService {
    private final Logger logger = LoggerFactory.getLogger(DallEService.class);

    @Value("${dalle.default_image_size}")
    private String DEFAULT_IMAGE_SIZE;

    @Value("${dalle.response_format}")
    private String RESPONSE_FORMAT;

    private final OpenAIInterface openAIInterface;

    @Autowired
    public DallEService(OpenAIInterface openAIInterface) {
        this.openAIInterface = openAIInterface;
    }

    public void downloadImagesFromPromptAndNumber(String prompt, int numberOfImages) {
        ImageRequest imageRequest = new ImageRequest(
                prompt,
                numberOfImages,
                DEFAULT_IMAGE_SIZE,
                RESPONSE_FORMAT);
        ImageResponse imageResponse = openAIInterface.getImageResponse(imageRequest);
        List<Boolean> results = imageResponse.data().stream()
                .map(Image::b64_json)
                .map(FileUtils::writeImageToFile)
                .toList();
        logger.info("Wrote {} images to disk", results.size());
    }
}