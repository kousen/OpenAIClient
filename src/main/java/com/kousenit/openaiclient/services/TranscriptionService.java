package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranscriptionService {

    public int MAX_ALLOWED_SIZE;

    private final Logger logger = LoggerFactory.getLogger(TranscriptionService.class);

    public static final String WORD_LIST = String.join(", ",
            List.of("Kousen", "GPT-3", "GPT-4", "DALL-E",
                    "Midjourney", "AssertJ", "Mockito", "JUnit", "Java", "Kotlin", "Groovy", "Scala",
                    "IOException", "RuntimeException", "UncheckedIOException", "UnsupportedAudioFileException",
                    "assertThrows", "assertTrue", "assertEquals", "assertNull", "assertNotNull", "assertThat",
                    "Tales from the jar side", "Spring Boot", "Spring Framework", "Spring Data", "Spring Security"));

    @Value("${whisper.model}")
    private String WHISPER_MODEL;

    private final RestClient restClient;
    private final WavFileSplitter splitter;

    @Autowired
    public TranscriptionService(@Qualifier("openAIRestClient") RestClient restClient, WavFileSplitter splitter,
                                @Value("${whisper.max-allowed-size}") DataSize maxAllowedSize) {
        this.restClient = restClient;
        this.splitter = splitter;
        this.MAX_ALLOWED_SIZE = (int) maxAllowedSize.toBytes();
    }

    public ResponseEntity<String> transcribeAudio(String filename, String prompt) throws IOException {
        File file = new File(filename);
        var audioResource = new ByteArrayResource(Files.readAllBytes(file.toPath())) {
            @Override
            public String getFilename() {
                return file.getName();
            }
        };

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", audioResource);
        formData.add("model", WHISPER_MODEL);
        formData.add("prompt", prompt);
        formData.add("response_format", "text");

        return transcribeAudioImpl(formData);
    }

    public ResponseEntity<String> transcribeAudio(Resource audioResource, String prompt) {
        String filename = audioResource.getFilename();
        if (filename == null) {
            throw new IllegalArgumentException("The provided Resource does not have a filename.");
        }

        try {
            // Copy the Resource content to a temporary file to ensure the file is accessible by path.
            Path tempFilePath = Files.createTempFile("audio-", ".wav");
            Files.copy(audioResource.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            // Delegate to the overload that takes a filename.
            ResponseEntity<String> response = transcribeAudio(tempFilePath.toString(), prompt);

            // Clean up the temporary file after processing.
            Files.deleteIfExists(tempFilePath);

            return response;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private ResponseEntity<String> transcribeAudioImpl(MultiValueMap<String, Object> formData) {
        return restClient.post()
                .uri("/v1/audio/transcriptions")
                .body(formData)
                .retrieve()
                .toEntity(String.class);
    }

    public String transcribeAudioFile(String fileName) throws IOException {
        File file = new File(fileName);

        // Collect the transcriptions of each chunk
        List<String> transcriptions = new ArrayList<>();

        // First prompt is the word list
        String prompt = WORD_LIST;

        if (file.length() <= MAX_ALLOWED_SIZE) {
            String transcription = transcribeAudio(file.getAbsolutePath(), prompt).getBody();
            assert transcription != null;
            transcriptions = List.of(transcription);
        } else {
            List<File> chunks = splitter.splitWavFileIntoChunks(file);
            for (File chunk : chunks) {
                logger.info("Transcribing {} (size: {})", chunk.getName(), chunk.length());
                // Subsequent prompts are the previous transcriptions
                String transcription = transcribeAudio(chunk.getAbsolutePath(), prompt).getBody();
                transcriptions.add(transcription);
                prompt = transcription;

                // After transcribing, no longer need the chunk
                if (!chunk.delete()) {
                    System.out.println("Failed to delete " + chunk.getName());
                }
            }
        }

        // Join the individual transcripts and write to a file
        String transcription = String.join(" ", transcriptions);
        String fileNameWithoutPath = fileName.substring(
                fileName.lastIndexOf("/") + 1);
        FileUtils.writeTextToFile(transcription,
                fileNameWithoutPath.replace(".wav", ".txt"));
        return transcription;
    }
}
