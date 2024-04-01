package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class VoiceSerializationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest(name = "Serializing {index} => {0}")
    @EnumSource(Voice.class)
    void serializeVoice(Voice voice) {
        try {
            String json = objectMapper.writeValueAsString(voice);
            JSONAssert.assertEquals("\"%s\"".formatted(voice.name().toLowerCase()),
                    json, false);
        } catch (Exception e) {
            fail(e);
        }
    }

    @ParameterizedTest(name = "Deserializing {index} => {0}")
    @EnumSource(Voice.class)
    void deserializeVoice(Voice voice) {
        try {
            String json = "\"%s\"".formatted(voice.name().toLowerCase());
            Voice deserialized = objectMapper.readValue(json, Voice.class);
            if (voice != deserialized) {
                fail("Expected %s, got %s".formatted(voice, deserialized));
            }
        } catch (Exception e) {
            fail(e);
        }
    }
}