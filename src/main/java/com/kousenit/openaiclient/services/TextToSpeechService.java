package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.TTSRequest;
import com.kousenit.openaiclient.json.Voice;
import com.kousenit.openaiclient.util.FileUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.Set;

@Service
public class TextToSpeechService {
    public final static String TTS_1 = "tts-1";
    public final static String TTS_1_HD = "tts-1-hd";

    private static final Voice DEFAULT_VOICE = Voice.ALLOY;
    private static final String DEFAULT_MODEL = TTS_1;

    private final Logger logger = LoggerFactory.getLogger(TextToSpeechService.class);

    private final OpenAIInterface openAIInterface;

    private final Validator validator;

    @Autowired
    public TextToSpeechService(OpenAIInterface openAIInterface, Validator validator) {
        this.openAIInterface = openAIInterface;
        this.validator = validator;
    }

    public byte[] getAudioResponse(TTSRequest ttsRequest) {
        validateRequest(ttsRequest);
        byte[] bytes = openAIInterface.getTextToSpeechResponse(ttsRequest);
        String fileName = FileUtils.writeSoundBytesToFile(bytes);
        logger.info("Saved {} to {}", fileName, FileUtils.AUDIO_DIRECTORY);
        return bytes;
    }

    public byte[] getAudioResponse(String prompt) {
        return getAudioResponse(new TTSRequest(DEFAULT_MODEL, prompt, DEFAULT_VOICE));
    }

    public byte[] getAudioResponse(String prompt, Voice voice) {
        return getAudioResponse(new TTSRequest(DEFAULT_MODEL, prompt, voice));
    }

    public byte[] getAudioResponse(String model, String prompt, Voice voice) {
        return getAudioResponse(new TTSRequest(model, prompt, voice));
    }

    public void playMp3UsingJLayer(String fileName) {
        BufferedInputStream buffer = new BufferedInputStream(
                Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("audio/%s".formatted(fileName))));
        playMp3(buffer);
    }

    public void playMp3UsingJLayer(byte[] bytes) {
        var buffer = new BufferedInputStream(new ByteArrayInputStream(bytes));
        playMp3(buffer);
    }

    private void playMp3(BufferedInputStream buffer) {
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
        playMp3UsingJLayer(bytes);
    }

    private void validateRequest(TTSRequest ttsRequest) {
        Set<ConstraintViolation<TTSRequest>> violations = validator.validate(ttsRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

