package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Role;
import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;
import static com.kousenit.openaiclient.json.OpenAIRecords.ModelList.*;

@Service
public class OpenAIService {
    public static final String GPT35 = "gpt-3.5-turbo";
    public static final String GPT4 = "gpt-4-turbo";
    public static final String GPT4O = "gpt-4o";
    public static final String GPT4O_MINI = "gpt-4o-mini";

    private final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    public static final String WORD_LIST = String.join(", ",
            List.of("Kousen", "GPT-3", "GPT-4", "DALL-E",
                    "Midjourney", "AssertJ", "Mockito", "JUnit", "Java", "Kotlin", "Groovy", "Scala",
                    "IOException", "RuntimeException", "UncheckedIOException",
                    "UnsupportedAudioFileException", "assertThrows", "assertTrue", "assertEquals",
                    "assertNull", "assertNotNull", "assertThat", "Tales from the jar side", "Spring Boot",
                    "Spring Framework", "Spring Data", "Spring Security"));

    @Value("${whisper.model}")
    private String WHISPER_MODEL;

    public int maxAllowedSize;

    private final WavFileSplitter splitter;

    private final OpenAIInterface openAIInterface;

    @Autowired
    public OpenAIService(OpenAIInterface openAIInterface, WavFileSplitter splitter,
                         @Value("${whisper.max-allowed-size}") DataSize maxAllowedSize) {
        this.openAIInterface = openAIInterface;
        this.splitter = splitter;
        this.maxAllowedSize = (int) maxAllowedSize.toBytes();
    }

    public List<Model> getModels() {
        return openAIInterface.listModels()
                .data().stream()
                .sorted(Comparator.comparing(Model::id))
                .toList();
    }

    public List<String> getModelNames() {
        return openAIInterface.listModels()
                .data().stream()
                .map(Model::id)
                .sorted()
                .toList();
    }

    public String getChatResponse(String model, List<Message> messages, double temperature) {
        ChatRequest chatRequest = new ChatRequest(model, messages, temperature);
        ChatResponse response = openAIInterface.getChatResponse(chatRequest);
        logger.info("Usage: {}", response.usage());
        return response.choices().getFirst().message().content();
    }

    public String getChatResponse(String message) {
        return getChatResponse(GPT4, List.of(new Message(Role.USER, message)), 0.7);
    }

    public ChatRequest createChatRequestFromDefaults(String prompt) {
        return createChatRequest(prompt);
    }

    private ChatRequest createChatRequest(String prompt) {
        return new ChatRequest(OpenAIService.GPT4,
                List.of(new Message(Role.USER, prompt)),
                0.7);
    }

    public String getTranscription(Resource audioResource) {
        if (!audioResource.isFile()) {
            throw new UnsupportedOperationException("Resource must be a file");
        }

        // Collect the transcriptions of each chunk into a list
        List<String> transcriptions = new ArrayList<>();

        // First prompt is the word list
        String prompt = WORD_LIST;
        try {
            long length = audioResource.getFile().length();
            if (length <= maxAllowedSize) {
                logger.info("Transcribing {}", audioResource.getFilename());
                String transcription = openAIInterface.getTranscriptionResponse(
                        audioResource, WHISPER_MODEL, WORD_LIST, "text");
                transcriptions = List.of(transcription);
            } else {
                List<Resource> chunks = splitter.splitWavResourceIntoChunks(audioResource);
                for (Resource chunk : chunks) {
                    int chunkIndex = chunks.indexOf(chunk) + 1;
                    logger.info("Transcribing chunk {} of {}: {}",
                            chunkIndex, chunks.size(), chunk.getFilename());

                    // subsequent prompts are the previous transcriptions
                    String transcription = openAIInterface.getTranscriptionResponse(
                            chunk, WHISPER_MODEL, prompt, "text");
                    transcriptions.add(transcription);
                    prompt = transcription;

                    // Ensure deletion of chunk file
                    if (chunk.getFile().delete()) {
                        logger.info("Successfully deleted chunk file: {}", chunk.getFilename());
                    } else {
                        logger.error("Failed to delete chunk file: {}", chunk.getFilename());
                    }
                }
            }
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }

        // Join the individual transcripts and write to a file
        String transcription = String.join(" ", transcriptions);
        return saveResultsToFile(audioResource, transcription);
    }

    public String getTranslation(Resource audioResource) {
        logger.info("Translating {}", audioResource.getFilename());
        String translation = openAIInterface.getTranslationResponse(
                audioResource, WHISPER_MODEL, "", "text");
        return saveResultsToFile(audioResource, translation);
    }

    private String saveResultsToFile(Resource audioResource, String transcription) {
        String fileName = audioResource.getFilename();
        assert fileName != null;
        String fileNameWithoutPath = fileName.substring(
                fileName.lastIndexOf("/") + 1);
        String extension = fileNameWithoutPath.substring(
                fileNameWithoutPath.lastIndexOf("."));
        FileUtils.writeTextToFile(transcription,
                fileNameWithoutPath.replace(extension, ".txt"));
        return transcription;
    }
}
