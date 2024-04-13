package com.kousenit.openaiclient.controllers;

import com.kousenit.openaiclient.json.OpenAIRecords;
import com.kousenit.openaiclient.json.Role;
import com.kousenit.openaiclient.services.MistralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MistralController {

    private final MistralService service;

    @Autowired
    public MistralController(MistralService service) {
        this.service = service;
    }

    @GetMapping("/chat") // localhost:8080/chat?question=Who is the most renowed French painter?
    public String askQuestion(@RequestParam String question) {
        var response = service.complete(MistralService.MISTRAL_SMALL_LATEST,
                List.of(new OpenAIRecords.Message(Role.USER, question)));
        System.out.println(response);
        return response.choices().getFirst().message().content();
    }

    public record Question(String question) {}

    @PostMapping("/chat")
    public String askQuestionPost(@RequestBody Question question) {
        return askQuestion(question.question());
    }


}
