package com.kousenit.openaiclient.json;

import com.kousenit.openaiclient.services.TextToSpeechService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TTSRequestTest {
    @Autowired
    private Validator validator;

    @Test
    void testValidModels() {
        TTSRequest request = new TTSRequest(TextToSpeechService.TTS_1, "Hello, world", Voice.ALLOY);
        assertTrue(validator.validate(request).isEmpty());
        request = new TTSRequest(TextToSpeechService.TTS_1_HD, "Hello, world", Voice.ALLOY);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testInvalidModel() {
        TTSRequest request = new TTSRequest("tts-2", "Hello, world", Voice.ALLOY);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testValidSpeed() {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                Voice.ALLOY, ResponseFormat.MP3, 1.0);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testSpeedTooLow() {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                Voice.ALLOY, ResponseFormat.MP3, 0.0);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testSpeedTooHigh() {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                Voice.ALLOY, ResponseFormat.MP3, 5.0);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testBlankInput() {
        TTSRequest request = new TTSRequest("tts-1", "  ",
                Voice.ALLOY, ResponseFormat.MP3, 1.0);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void testLongInput() {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world".repeat(1000),
                Voice.ALLOY, ResponseFormat.MP3, 1.0);
        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest(name = "Testing response format {0}")
    @EnumSource(ResponseFormat.class)
    void testResponseFormats(ResponseFormat format) {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                Voice.ALLOY, format, 1.0);
        assertTrue(validator.validate(request).isEmpty());
    }
}