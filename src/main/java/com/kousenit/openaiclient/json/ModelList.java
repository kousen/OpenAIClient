package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record ModelList(List<Model> data) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Model(String id, long created, String ownedBy) {

        @Override
        public String toString() {
            return "Model{id='%s', created=%s, ownedBy='%s'}".formatted(
                    id, LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(created), ZoneId.systemDefault()),
                    ownedBy);
        }
    }
}
