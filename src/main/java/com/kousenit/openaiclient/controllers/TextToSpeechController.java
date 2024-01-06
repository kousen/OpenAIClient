package com.kousenit.openaiclient.controllers;

import com.kousenit.openaiclient.json.Voice;
import com.kousenit.openaiclient.services.TextToSpeechService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TextToSpeechController {
    private final TextToSpeechService service;

    public TextToSpeechController(TextToSpeechService service) {
        this.service = service;
    }

    @PostMapping("/tts")
    public ResponseEntity<String> convertTextToSpeech(@RequestBody String text) {
        service.getAudioResponse(TextToSpeechService.TTS_1_HD, text, Voice.randomVoice());
        return ResponseEntity.ok("(%s...) converted to mp3".formatted(text.substring(0, 24)));
    }

    @PostMapping("/tts/play")
    public ResponseEntity<String> convertTextAndPlay(@RequestBody String text) {
        service.createAndPlay(text, Voice.randomVoice());
        return ResponseEntity.ok("(%s...) converted to mp3".formatted(text.substring(0, 24)));
    }
}
