package com.kousenit.openaiclient.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WavFileSplitterTest {
    @Autowired
    private WavFileSplitter splitter;

    //@Value("${whisper.max_allowed_size}")
    public int MAX_ALLOWED_SIZE = 25 * 1024 * 1024;

    @Test
    void splitWavFileIntoChunks(@Value("classpath:audio/EarningsCall.wav") Resource wavFile) throws IOException {
        List<File> files = splitter.splitWavFileIntoChunks(wavFile.getFile());
        assertEquals(3, files.size());
        files.forEach(f -> assertThat(f).size().isLessThan(MAX_ALLOWED_SIZE));
        files.stream()
                .map(File::delete)
                .forEach(Assertions::assertTrue);
    }
}