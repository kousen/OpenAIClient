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
    public static final String TEXT_DIRECTORY = "src/main/resources/text";
    public static final String IMAGE_DIRECTORY = "src/main/resources/images";
    public static final String AUDIO_DIRECTORY = "src/main/resources/audio";
    public static final String VIDEO_DIRECTORY = "src/main/resources/video";

    private static int counter;

    public static void writeTextToFile(String textData, String fileName) {
        Path directory = Paths.get(TEXT_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            Files.deleteIfExists(filePath);
            Files.writeString(filePath, textData, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to %s%n", fileName, TEXT_DIRECTORY);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing text to file", e);
        }
    }

    public static boolean writeImageToFile(String imageData) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("image_%s_%d.png", timestamp, counter++);
        Path directory = Paths.get(IMAGE_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            byte[] bytes = Base64.getDecoder().decode(imageData);
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to %s%n", fileName, IMAGE_DIRECTORY);
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing image to file", e);
        }
    }

    public static boolean writeImageBytesToFile(byte[] bytes) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("image_%s_%d.png", timestamp, counter++);
        Path directory = Paths.get(IMAGE_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to %s%n", fileName, IMAGE_DIRECTORY);
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing image to file", e);
        }
    }

    public static String writeSoundBytesToFile(byte[] bytes) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("audio_%s.mp3", timestamp);
        Path directory = Paths.get(AUDIO_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            return fileName;
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing audio to file", e);
        }
    }


    public static void writeVideoBytesToFile(String video) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("video_%s.mp4", timestamp);
        Path directory = Paths.get(VIDEO_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            byte[] bytes = Base64.getDecoder().decode(video);
            Files.write(filePath, bytes, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to %s%n", fileName, VIDEO_DIRECTORY);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing video to file", e);
        }
    }

    public static void writeResponseDataToFile(String string) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("response_%s.txt", timestamp);
        Path directory = Paths.get(TEXT_DIRECTORY);
        Path filePath = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            Files.writeString(filePath, string, StandardOpenOption.CREATE_NEW);
            System.out.printf("Saved %s to %s%n", fileName, TEXT_DIRECTORY);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing response data to file", e);
        }
    }
}
