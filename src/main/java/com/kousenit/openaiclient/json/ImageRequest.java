package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;

// prompt has max length of 1000 characters
// n defaults to 1, which happens if n is null. Otherwise must be between 1 and 10.
// size defaults to 1024x1024. Also valid are 256x256 and 512x512
// NOTE: response_format can be "url" or "b64_json". Default is url.
public record ImageRequest(
        String model,
        String prompt,
        Integer n,      // must be 1 for DALL-E-3
        String quality, // "standard" or "hd" for DALL-E-3
        String size,    // 1024x1024 or higher for DALL-E-3
        @JsonProperty("response_format") String responseFormat) {
}
