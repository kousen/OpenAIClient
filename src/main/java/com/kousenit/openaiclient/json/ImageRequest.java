package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kousenit.openaiclient.services.DallEService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// prompt has max length of 1000 characters
// n defaults to 1, which happens if n is null. Otherwise must be between 1 and 10.
// size defaults to 1024x1024. Also valid are 256x256 and 512x512
// NOTE: response_format can be "url" or "b64_json". Default is url.
public record ImageRequest(
        @Pattern(regexp = "dall-e-[23]") String model,
        @Size(max = 1000) String prompt,
        int n,          // must be 1 for DALL-E-3
        String quality, // "standard" or "hd" for DALL-E-3
        String size,    // 1024x1024 or higher for DALL-E-3
        @JsonProperty("response_format") String responseFormat) {

    public ImageRequest {
        validateNumberOfImages();
    }

    private void validateNumberOfImages() {
        if (model().equals(DallEService.DALL_E_3) && n() != 1) {
            throw new IllegalArgumentException("DALL-E-3 only supports n = 1");
        }
    }
}
