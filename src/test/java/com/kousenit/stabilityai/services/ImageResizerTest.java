package com.kousenit.stabilityai.services;

import com.kousenit.stabilityai.util.ImageResizer;
import org.junit.jupiter.api.Test;

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

}