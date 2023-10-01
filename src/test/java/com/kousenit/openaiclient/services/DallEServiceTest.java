package com.kousenit.openaiclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DallEServiceTest {
    @Autowired
    private DallEService dallEService;

    @Test
    void downloadImagesFromPromptAndNumber() {
        String prompt = """
                A photorealistic image of a happy robot
                jumping up and down on springs
                """;
        dallEService.downloadImagesFromPromptAndNumber(prompt, 4);
    }
}