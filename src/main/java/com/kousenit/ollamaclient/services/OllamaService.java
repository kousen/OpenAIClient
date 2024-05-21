package com.kousenit.ollamaclient.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kousenit.ollamaclient.json.OllamaRecords.*;
import static com.kousenit.ollamaclient.json.OllamaRecords.ModelList.OllamaModel;

@Service
public class OllamaService {
    public static final String DEFAULT_MODEL = "orca-mini";
    public static final String LLAVA = "llava";
    public static final String LLAVA_LLAMA3 = "llava-llama3";
    public static final String LLAVA_PHI3 = "llava-phi3";
    public static final String BAKLLAVA = "bakllava";
    public static final String MOONDREAM = "moondream";
    public static final String ORCA_MINI = "orca-mini";
    public static final String LLAMA3 = "llama3";
    public static final String GEMMA = "gemma";

    private final OllamaInterface ollamaInterface;

    public OllamaService(OllamaInterface ollamaInterface) {
        this.ollamaInterface = ollamaInterface;
    }

    public List<OllamaModel> getModels() {
        return ollamaInterface.getModels().models();
    }

    public String chat(String model, String question) {
        var request = new OllamaChatRequest(model,
                List.of(new Message("user", question)), false);
        var response = ollamaInterface.chat(request);
        return response.message().content();
    }

    public String chatWithDefaultModel(String question) {
        return chat(DEFAULT_MODEL, question);
    }

    public String conversation(String model, String... strings) {
        if (strings.length % 2 == 0) {
            throw new IllegalArgumentException(
                    """
                    Odd number of strings required, alternating
                    messages between 'user' and 'assistant'.""");
        }
        List<Message> messages = IntStream.range(0, strings.length)
                .mapToObj(i -> new Message(
                        i % 2 == 0 ? "user" : "assistant", strings[i]))
                .collect(Collectors.toList());
        var request = new OllamaChatRequest(model, messages, false);
        var response = ollamaInterface.chat(request);
        return response.message().content();
    }

    public String generate(OllamaGenerateRequest request) {
        return switch (request) {
            case OllamaGenerateImageRequest imageRequest -> {
                System.out.printf("Reading image using %s%n", imageRequest.model());
                yield ollamaInterface.generate(imageRequest).response();
            }
            case OllamaGenerateTextRequest textRequest -> {
                System.out.printf("Generating text response from %s...%n", textRequest.model());
                yield ollamaInterface.generate(textRequest).response();
            }
        };
    }
}
