package com.kousenit.openaiclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TranscriptionServiceTest {

    @Autowired
    private TranscriptionService service;

    @Test
    void transcribeAudioFromResource(@Value("classpath:audio/AssertJExceptions.wav") Resource wavFile) {
        ResponseEntity<String> response =
                service.transcribeAudio(wavFile, "AssertJ");
        assert response != null;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(Objects.requireNonNull(response.getBody()));
    }

    @Test
    void transcribeAudioFromFileName(@Value("classpath:audio/AssertJExceptions.wav") File wavFile) {
        try {
            ResponseEntity<String> response =
                    service.transcribeAudio(wavFile.getAbsolutePath(), "AssertJ");
            assert response != null;
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);
            System.out.println(response.getBody());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void transcribeAudioFromLongerFile() {
        try {
            String transcription = service.transcribeAudioFile(
                    "src/main/resources/audio/EarningsCall.wav");
            assertThat(transcription)
                    .isNotBlank()
                    .hasLineCount(3);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
