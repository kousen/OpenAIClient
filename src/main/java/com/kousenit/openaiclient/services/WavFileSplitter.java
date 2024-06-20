package com.kousenit.openaiclient.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class WavFileSplitter {
    @Value("${whisper.max_chunk_size_bytes}")
    public int MAX_CHUNK_SIZE_BYTES;

    public List<Resource> splitWavResourceIntoChunks(Resource sourceResource)
            throws IOException, UnsupportedAudioFileException {
        // Ensure the source resource exists and is readable
        if (!sourceResource.exists()) {
            throw new IllegalArgumentException("Source resource not found");
        }

        Path sourcePath = sourceResource.getFile().toPath();
        List<Resource> chunks = new ArrayList<>();

        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(sourcePath.toFile())) {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourcePath.toFile());
            AudioFormat format = fileFormat.getFormat();
            long totalFrames = inputStream.getFrameLength();
            int frameSize = format.getFrameSize();
            long framesPerChunk = MAX_CHUNK_SIZE_BYTES / frameSize;

            byte[] buffer = new byte[(int) (framesPerChunk * frameSize)];
            int chunkCounter = 1;

            while (totalFrames > 0) {
                long framesToRead = Math.min(totalFrames, framesPerChunk);
                int bytesRead = inputStream.read(buffer, 0, (int) (framesToRead * frameSize));
                if (bytesRead > 0) {
                    // Create a temporary file for the chunk
                    Path chunkPath = Files.createTempFile("chunk-" + chunkCounter, ".wav");

                    try (AudioInputStream partStream = new AudioInputStream(
                            new ByteArrayInputStream(buffer, 0, bytesRead), format, framesToRead)) {
                        AudioSystem.write(partStream, AudioFileFormat.Type.WAVE, chunkPath.toFile());
                    }

                    // Convert Path to Resource
                    Resource chunkResource = new FileSystemResource(chunkPath.toFile());
                    chunks.add(chunkResource);
                    chunkCounter++;
                }
                totalFrames -= framesToRead;
            }
        }

        return chunks;
    }

    public List<File> splitWavFileIntoChunks(File sourceWavFile) {
        List<File> chunks = new ArrayList<>();
        int chunkCounter = 1;

        if (!sourceWavFile.exists()) {
            throw new IllegalArgumentException("Source file not found at: " + sourceWavFile.getAbsolutePath());
        }

        try (var inputStream = AudioSystem.getAudioInputStream(sourceWavFile)) {
            long totalFrames = inputStream.getFrameLength(); // Total frames in the source wav file
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourceWavFile);
            AudioFormat format = fileFormat.getFormat();

            // Calculate the maximum number of frames for each chunk
            int frameSize = format.getFrameSize(); // Number of bytes in each frame
            long framesPerChunk = MAX_CHUNK_SIZE_BYTES / frameSize;

            byte[] buffer = new byte[(int) (framesPerChunk * frameSize)];

            while (totalFrames > 0) {
                long framesInThisFile = Math.min(totalFrames, framesPerChunk);
                int bytesRead = inputStream.read(buffer, 0, (int) (framesInThisFile * frameSize));
                if (bytesRead > 0) {
                    File chunkFile = new File(sourceWavFile.getAbsolutePath().replace(
                            ".wav", "-%d.wav".formatted(chunkCounter)));
                    try (var partStream = new AudioInputStream(
                            new ByteArrayInputStream(buffer, 0, bytesRead),
                            format,
                            framesInThisFile)) {
                        AudioSystem.write(partStream, AudioFileFormat.Type.WAVE, chunkFile);
                    }
                    chunks.add(chunkFile);
                    chunkCounter++;
                }
                totalFrames -= framesInThisFile;
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        return chunks;
    }
}