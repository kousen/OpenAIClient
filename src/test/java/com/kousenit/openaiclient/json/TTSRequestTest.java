package com.kousenit.openaiclient.json;

import com.kousenit.openaiclient.services.TextToSpeechService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("current")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TTSRequestTest {
    @Autowired
    private Validator validator;

    // Convenient factory methods
    private TTSRequest createRequestWithSpeed(double speed) {
        return new TTSRequest(TextToSpeechService.TTS_1, "Hello", Voice.ALLOY, ResponseFormat.MP3, speed);
    }

    private TTSRequest createRequestWithModel(String model) {
        return new TTSRequest(model, "Hello", Voice.ALLOY);
    }

    private TTSRequest createRequestWithInput(String input) {
        return new TTSRequest(TextToSpeechService.TTS_1, input, Voice.ALLOY);
    }

    @ParameterizedTest(name = "Model {0}")
    @ValueSource(strings = {TextToSpeechService.TTS_1, TextToSpeechService.TTS_1_HD})
    void valid_models(String model) {
        TTSRequest request = createRequestWithModel(model);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void invalid_model() {
        TTSRequest request = createRequestWithModel("tts-2");
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void overloaded_constructors() {
        // 3-arg constructor
        TTSRequest request = new TTSRequest(TextToSpeechService.TTS_1, "Hello, world", Voice.ALLOY);
        assertTrue(validator.validate(request).isEmpty());

        // 2-arg constructor
        request = new TTSRequest("Hello, world", Voice.ALLOY);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void speeds() {
        assertAll(
                // Upper boundary
                () -> assertDoesNotThrow(() -> createRequestWithSpeed(0.25)),

                // Lower boundary
                () -> assertDoesNotThrow(() -> createRequestWithSpeed(4.0)),

                // Middle
                () -> assertDoesNotThrow(() -> createRequestWithSpeed(1.0)),

                // Below lower boundary
                () -> assertThrows(IllegalArgumentException.class, () -> createRequestWithSpeed(0.24)),

                // Above upper boundary
                () -> assertThrows(IllegalArgumentException.class, () -> createRequestWithSpeed(4.01))
        );
    }

    @Test
    void blank_input_not_valid() {
        TTSRequest request = new TTSRequest("tts-1", "  ",
                Voice.ALLOY, ResponseFormat.MP3, 1.0);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void long_input_not_valid() {
        assertAll(
                () -> assertTrue(validator.validate(
                        createRequestWithInput("A".repeat(4096))).isEmpty()), // Max valid length
                () -> assertFalse(validator.validate(
                        createRequestWithInput("A".repeat(4097))).isEmpty())  // Too long
        );
    }

    @ParameterizedTest(name = "Response format {0}")
    @EnumSource(ResponseFormat.class)
    void response_format(ResponseFormat format) {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                Voice.ALLOY, format, 1.0);
        assertTrue(validator.validate(request).isEmpty());
    }

    @ParameterizedTest(name = "Voice {0}")
    @EnumSource(Voice.class)
    void voice(Voice voice) {
        TTSRequest request = new TTSRequest("tts-1", "Hello, world",
                voice, ResponseFormat.MP3, 1.0);
        assertTrue(validator.validate(request).isEmpty());
    }
}