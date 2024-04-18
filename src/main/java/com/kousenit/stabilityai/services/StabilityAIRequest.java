package com.kousenit.stabilityai.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

public class StabilityAIRequest {
    private static final String API_KEY = System.getenv("STABILITY_API_KEY");

    public void requestStableImage(String prompt) throws IOException, InterruptedException {
        HttpResponse<Path> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.stability.ai/v2beta/stable-image/generate/sd3"))
                    .header("Authorization", "Bearer %s".formatted(API_KEY))
                    .header("Accept", "image/*")
                    .header("Content-Type", "multipart/form-data; boundary=myBoundary")
                    .POST(HttpRequest.BodyPublishers.ofString("""
                            --myBoundary
                            Content-Disposition: form-data; name="prompt"
                            
                            %s
                            --myBoundary
                            Content-Disposition: form-data; name="output_format"
                            
                            png
                            --myBoundary--
                            """.formatted(prompt))
                    )
                    .build();

            String fileName = "stabilityai_%s_%s.png".formatted(
                    LocalDate.now(),
                    prompt.replaceAll("\\s+", "_").substring(0, 10));

            Path filePath = Paths.get("src/main/resources/images", fileName);
            System.out.println("Attempting to write to: " + filePath.toAbsolutePath());

            response = client.send(request, HttpResponse.BodyHandlers.ofFile(
                    filePath,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING));

            if (response.statusCode() == 200) {
                System.out.println(filePath.toAbsolutePath() + " downloaded successfully.");
                response.body();
            } else {
                throw new IOException("Error: " + response.statusCode());
            }

        }

    }

}
