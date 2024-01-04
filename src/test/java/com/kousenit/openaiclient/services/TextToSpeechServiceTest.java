package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Voice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TextToSpeechServiceTest {
    @Autowired
    private TextToSpeechService service;

    private final String audioPrompt = """
            The YouTube channel, "Tales from the jar side" is your best
            source for learning about Java, Spring, and other open source
            technologies, especially when combined with AI tools.
            The companion newsletter of the same name is also a lot of fun.
            """.replaceAll("\\s+", " ").trim();

    @Test
    void getAudioResponse() {
        service.getAudioResponse(audioPrompt);
    }

    @Test
    void getAudioResponseWithVoice() {
        service.getAudioResponse(audioPrompt, Voice.SHIMMER);
    }

    @Test
    void getAudioResponseWithModelAndVoice() {
        service.getAudioResponse(TextToSpeechService.TTS_1_HD, audioPrompt, Voice.FABLE);
    }

    @Test
    void playMp3UsingJLayer() {
        service.playMp3UsingJLayer("tftjs.mp3");
    }

    @Test
    void createAndPlay() {
        Voice voice = Voice.randomVoice();
        System.out.println("Using voice " + voice);
        service.createAndPlay(audioPrompt, voice);
    }

    @Test
    // NOTE: The JUnit library is referred to here as J-Unit to make
    // the audio come out correctly.
    void createAndPlay_includingTechnicalWords() {
        Voice voice = Voice.randomVoice();
        System.out.println("Using voice " + voice);
        service.createAndPlay("""
                This application uses Spring Boot's HTTP exchange interfaces,
                as well as Java records, text blocks, enums, and the var
                reserved type name, to access the OpenAI API web service.
                
                The embedded JSON parser is Jackson, customized to use
                Java records instead of POJOs, and the enums are translated
                to lowercase using the @JsonValue annotation.
                
                Testing is done with J-Unit 5 and the AssertJ testing library.
                
                Since the cost of the base TTS model is only 1.5 cents per
                1000 characters, this test cost less than a penny to run.
                """, voice);
    }
}