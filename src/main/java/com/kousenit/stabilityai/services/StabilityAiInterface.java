package com.kousenit.stabilityai.services;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static com.kousenit.stabilityai.json.StabilityAiRecords.VideoId;
import static com.kousenit.stabilityai.json.StabilityAiRecords.VideoResponse;

@HttpExchange("/v2beta")
public interface StabilityAiInterface {

    @PostExchange(value = "/stable-image/generate/sd3",
            accept = "image/*",
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] requestStableImage(
            @RequestPart String prompt,
            @RequestPart String model,
            @RequestPart String aspect_ratio,
            @RequestPart String output_format);

    @PostExchange(value = "/stable-image/generate/ultra",
            accept = "image/*",
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] requestStableImageUltra(
            @RequestPart String prompt,
            @RequestPart String aspect_ratio,
            @RequestPart String output_format);

    @PostExchange(value = "/image-to-video",
            accept = MediaType.APPLICATION_JSON_VALUE,
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    VideoId requestImageToVideo(@RequestPart Resource image,
                                @RequestPart double cfg_scale,
                                @RequestPart long motion_bucket_id);

    @GetExchange(value = "/image-to-video/result/{id}",
            accept = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<VideoResponse> getVideoStatus(@PathVariable String id);
}
