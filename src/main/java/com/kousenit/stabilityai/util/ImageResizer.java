package com.kousenit.stabilityai.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageResizer {

    public static void resizeImage(
            String inputImagePath,
            String outputImagePath,
            int scaledWidth,
            int scaledHeight) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        BufferedImage outputImage = resize(inputImage, scaledWidth, scaledHeight);
        File outputFile = new File(outputImagePath);
        ImageIO.write(outputImage, "PNG", outputFile);
    }

    // Resize image from byte array and return as Resource
    public static Resource resizeImageAsResource(byte[] inputData, int scaledWidth, int scaledHeight) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
        BufferedImage inputImage = ImageIO.read(inputStream);
        BufferedImage outputImage = resize(inputImage, scaledWidth, scaledHeight);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return new ByteArrayResource(imageBytes);
    }

    // Common resizing logic
    private static BufferedImage resize(BufferedImage originalImage, int scaledWidth, int scaledHeight) {
        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
        return scaledImage;
    }
}


