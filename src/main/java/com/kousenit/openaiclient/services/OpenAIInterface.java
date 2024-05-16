package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.TTSRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

@HttpExchange("/v1")
public interface OpenAIInterface {

    // Models
    @GetExchange(value = "/models", accept = MediaType.APPLICATION_JSON_VALUE)
    ModelList listModels();

    // Chat
    @PostExchange(value = "/chat/completions",
            accept = MediaType.APPLICATION_JSON_VALUE,
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse getChatResponse(@RequestBody ChatRequest chatRequest);


    // Vision
    @PostExchange(value = "/chat/completions",
            accept = MediaType.APPLICATION_JSON_VALUE,
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse getVisionResponse(@RequestBody VisionService.ChatRequest visionRequest);

    // Images
    @PostExchange(value = "/images/generations",
            accept = MediaType.APPLICATION_JSON_VALUE,
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ImageResponse getImageResponse(@RequestBody ImageRequest imageRequest);

    // Text-to-Speech
    @PostExchange(value = "/audio/speech",
            accept = "audio/mpeg", contentType = MediaType.APPLICATION_JSON_VALUE)
    byte[] getTextToSpeechResponse(@RequestBody TTSRequest ttsRequest);

    // Speech-to-Text (transcription)
    @PostExchange(value = "/audio/transcriptions",
            accept = MediaType.TEXT_PLAIN_VALUE,
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    String getTranscriptionResponse(@RequestPart Resource file, @RequestPart String model,
                                    @RequestPart String prompt, @RequestPart String response_format);

    // Speech-to-Text (translation)
    @PostExchange(value = "/audio/translations",
            accept = MediaType.TEXT_PLAIN_VALUE,
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    String getTranslationResponse(@RequestPart Resource file, @RequestPart String model,
                                  @RequestPart String prompt, @RequestPart String response_format);
}
