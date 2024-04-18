package com.kousenit.stabilityai.services;

import com.kousenit.openaiclient.util.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class StabilityAiService {

    private final StabilityAiInterface stabilityAiInterface;

    public StabilityAiService(StabilityAiInterface stabilityAiInterface) {
        this.stabilityAiInterface = stabilityAiInterface;
    }

    public byte[] requestStabilityAiImage(String prompt) {
        byte[] bytes = stabilityAiInterface.requestStableImage(
                prompt, "sd3", "1:1", "png");
        FileUtils.writeImageBytesToFile(bytes);
        return bytes;
    }
}
