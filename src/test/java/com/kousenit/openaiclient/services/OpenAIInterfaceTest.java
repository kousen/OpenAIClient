package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ModelList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAIInterfaceTest {

    @Autowired
    private OpenAIInterface openAIInterface;

    @Test
    void listModelIds() {
        openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .forEach(System.out::println);
    }

    @Test
    void listGPTModels() {
        openAIInterface.listModels().data().stream()
                .filter(model -> model.id().contains("gpt"))
                .forEach(System.out::println);
    }

}