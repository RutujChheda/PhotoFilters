package com.github.rutujchheda.photofilters.converter;

import com.github.rutujchheda.photofilters.model.ConversionType;
import com.github.rutujchheda.photofilters.model.Photo;
import com.github.rutujchheda.photofilters.model.Pixel;
import com.github.rutujchheda.photofilters.model.RGB;
import com.github.rutujchheda.photofilters.util.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts an image to a Clarendon filtered version.
 */
public class ClarendonConverter implements PhotoConverter {

    public String convert(final Photo image, final String imageName) {
        List<Pixel> pixels = new ArrayList<>();

        for (Pixel pixel : image.getPixels()) {
            RGB rgb = pixel.getRGB();
            rgb = applyClarendonFilter(rgb);
            pixels.add(new Pixel(pixel.getX(), pixel.getY(), rgb));
        }

        Photo convertedImage = new Photo(pixels, image.getHeight(), image.getWidth(), image.getType());

        return PhotoUtil.savePhoto(convertedImage, imageName, ConversionType.CLARENDON);
    }

    private RGB applyClarendonFilter(RGB original) {
        // Increase contrast by 20%
        double contrast = 1.2;

        // Increase saturation by 20%
        double saturation = 1.2;

        // Increase brightness by 20%
        double brightness = 1.2;

        // Decrease exposure by 5%
        double exposure = 0.95;

        int newRed = (int) Math.min(255, (contrast * original.getRed()) * brightness * exposure);
        int newGreen = (int) Math.min(255, (contrast * original.getGreen()) * brightness * exposure);
        int newBlue = (int) Math.min(255, (contrast * original.getBlue()) * brightness * exposure);

        return new RGB(newRed, newGreen, newBlue, 255);
    }
}