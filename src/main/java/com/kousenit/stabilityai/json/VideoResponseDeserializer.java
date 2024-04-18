package com.kousenit.stabilityai.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.kousenit.stabilityai.json.StabilityAiRecords.*;

public class VideoResponseDeserializer extends StdDeserializer<VideoResponse> {

    public VideoResponseDeserializer() {
        super(VideoResponse.class);
    }

    @Override
    public VideoResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.has("finish_reason")) {
            return jp.getCodec().treeToValue(node, VideoCompleted.class);
        } else if (node.has("status")) {
            return jp.getCodec().treeToValue(node, VideoInProgress.class);
        } else if (node.has("errors")) {
            return jp.getCodec().treeToValue(node, VideoErrors.class);
        }
        throw new IllegalArgumentException("Unknown video response type");
    }
}
