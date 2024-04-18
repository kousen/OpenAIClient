package com.kousenit.stabilityai.services;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v2beta")
public interface StabilityAiInterface {

    @PostExchange(value = "/stable-image/generate/sd3",
            accept = "image/*",
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] requestStableImage(@RequestPart String prompt,
                              @RequestPart String model,
                              @RequestPart String aspect_ratio,
                              @RequestPart String output_format);
}
