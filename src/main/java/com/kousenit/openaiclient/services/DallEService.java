package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@Service
public class DallEService {
    private final Logger logger = LoggerFactory.getLogger(DallEService.class);

    public static final String DALL_E_2 = "dall-e-2";
    public static final String DALL_E_3 = "dall-e-3";

    @Value("${dalle.default_image_size}")
    private String DEFAULT_IMAGE_SIZE;

    private final OpenAIInterface openAIInterface;

    @Autowired
    public DallEService(OpenAIInterface openAIInterface) {
        this.openAIInterface = openAIInterface;
    }

    public ImageRequest createImageRequest(
            String model, String prompt, int n,
            String quality, String size) {
        return new ImageRequest(model, prompt, n, quality, size, "b64_json");
    }

    public ImageResponse getImageResponse(ImageRequest imageRequest) {
        logger.info("Sending image request: {}", imageRequest);
        return openAIInterface.getImageResponse(imageRequest);
    }

    public ImageRequest createImageRequestFromDefaults(String prompt, int n) {
        return createImageRequest(DallEService.DALL_E_3, prompt, n,
                "standard", DEFAULT_IMAGE_SIZE);
    }

    public void downloadImagesFromPromptAndNumber(String model, String prompt, int numberOfImages) {
        ImageRequest imageRequest = createImageRequest(model, prompt, numberOfImages,
                "standard", DEFAULT_IMAGE_SIZE);
        ImageResponse imageResponse = getImageResponse(imageRequest);
        List<Boolean> results = imageResponse.data()
                .stream()
                .map(Image::b64_json)
                .map(FileUtils::writeImageToFile)
                .toList();
        logger.info("Wrote {} images to disk", results.size());
    }
}