package com.kousenit.stabilityai.json;

import java.util.List;

public class StabilityAiRecords {
    public record VideoId(String id) {
    }

    // Sealed interface
    public sealed interface VideoResponse
            permits VideoCompleted, VideoInProgress, VideoErrors {}

    // Record for a successful video generation response
    public record VideoCompleted(String video, String finish_reason, long seed) implements VideoResponse {}

    // Record for an in-progress video generation response
    public record VideoInProgress(String id, String status) implements VideoResponse {}

    // Record for an error response
    public record VideoErrors(String name, List<String> errors) implements VideoResponse {}

}
