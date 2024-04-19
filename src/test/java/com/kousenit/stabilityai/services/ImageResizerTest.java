package com.kousenit.stabilityai.services;

import com.kousenit.stabilityai.util.ImageResizer;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImageResizerTest {

    @Test
    void resizeImage() {
        try {
            String inputFile = "classpath:images/image_20240418151825_0.png";
            String outputFile = "classpath:images/resized_image.png";
            ImageResizer.resizeImage(inputFile, outputFile, 100, 100);
            System.out.println("Image was resized successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }

    @Test
    void testResizeImageAsResource() throws IOException {
        // Load test image as byte array
        byte[] inputBytes;
        try (InputStream imgStream = Files.newInputStream(Paths.get(
                "src/main/resources/images/image_20240419092834_0.png"))) {
            inputBytes = imgStream.readAllBytes();
        }

        // Resize image
        int scaledWidth = 768;
        int scaledHeight = 768;
        Resource resizedResource = ImageResizer.resizeImageAsResource(inputBytes, scaledWidth, scaledHeight);
        assertNotNull(resizedResource, "Resized resource is null");

        // Check result image dimensions
        InputStream resizedStream = resizedResource.getInputStream();
        BufferedImage resizedImage = ImageIO.read(resizedStream);
        assertEquals(scaledWidth, resizedImage.getWidth(), "Scaled width is incorrect");
        assertEquals(scaledHeight, resizedImage.getHeight(), "Scaled height is incorrect");
    }

}