package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.*;
import com.kousenit.openaiclient.util.FileUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class OpenAIService {
    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-1106-preview";

    public final static String TTS_1 = "tts-1";
    public final static String TTS_1_HD = "tts-1-hd";

    private final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final List<String> modelNames = new ArrayList<>();

    private final OpenAIInterface openAIInterface;

    private final Validator validator;

    @Autowired
    public OpenAIService(OpenAIInterface openAIInterface, Validator validator) {
        this.openAIInterface = openAIInterface;
        this.validator = validator;
        modelNames.addAll(getModelNames());
    }

    public List<String> getModelNames() {
        return openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .sorted()
                .toList();
    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        if (!modelNames.contains(model)) {
            throw new IllegalArgumentException("Invalid model name: " + model);
        }
        ChatRequest chatRequest = new ChatRequest(model, messages, temperature);
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info("Usage: {}", response.usage());
        return openAIInterface.extractStringResponse(response);
    }

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return openAIInterface.createChatRequest(prompt);
    }

    public ImageRequest createImageRequestFromDefaults(String prompt, int n, String size) {
        return openAIInterface.createImageRequest(prompt, n, size);
    }

    public byte[] getAudioResponse(TTSRequest ttsRequest) {
        Set<ConstraintViolation<TTSRequest>> violations = validator.validate(ttsRequest);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.toString());
        }
        byte[] bytes = openAIInterface.getTextToSpeechResponse(ttsRequest);
        String fileName = FileUtils.writeSoundBytesToFile(bytes);
        logger.info("Saved {} to {}", fileName, "src/main/resources/audio");
        return bytes;
    }

    public void getAudioResponse(String prompt) {
        TTSRequest ttsRequest = new TTSRequest(TTS_1, prompt, Voice.ALLOY);
        getAudioResponse(ttsRequest);
    }

    public void getAudioResponse(String prompt, Voice voice) {
        TTSRequest ttsRequest = new TTSRequest(TTS_1, prompt, voice);
        getAudioResponse(ttsRequest);
    }

    public void getAudioResponse(String model, String prompt, Voice voice) {
        TTSRequest ttsRequest = new TTSRequest(model, prompt, voice);
        getAudioResponse(ttsRequest);
    }

    public void playMp3UsingJLayer(String fileName) {
        BufferedInputStream buffer = new BufferedInputStream(
                Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("audio/%s".formatted(fileName))));
        try {
            Player player = new Player(buffer);
            player.play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void createAndPlay(String text, Voice voice) {
        TTSRequest ttsRequest = new TTSRequest(TTS_1_HD, text, voice);
        byte[] bytes = getAudioResponse(ttsRequest);
        var bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
        try {
            new Player(bufferedInputStream).play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

}
