package com.kousenit.openaiclient.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class FileUtils {
    private static int counter;

    public static void writeTextToFile(String textData, String fileName) {
        Path directory = Paths.get("src/main/resources/text");
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            Files.deleteIfExists(filePath);
            Files.writeString(filePath, textData, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to src/main/resources/text%n", fileName);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing text to file", e);
        }
    }

    public static boolean writeImageToFile(String imageData) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("image_%s_%d.png", timestamp, counter++);
        Path directory = Paths.get("src/main/resources/images");
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            byte[] bytes = Base64.getDecoder().decode(imageData);
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to src/main/resources/images%n", fileName);
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing image to file", e);
        }
    }

    public static String writeSoundBytesToFile(byte[] bytes) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("audio_%s.mp3", timestamp);
        Path directory = Paths.get("src/main/resources/audio");
        Path filePath = directory.resolve(fileName);
        try {
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            return fileName;
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing audio to file", e);
        }
    }


}
