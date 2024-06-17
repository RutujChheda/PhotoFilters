package com.github.rutujchheda.photofilters.converter;

import com.github.rutujchheda.photofilters.model.ConversionType;
import com.github.rutujchheda.photofilters.model.Photo;
import com.github.rutujchheda.photofilters.model.Pixel;
import com.github.rutujchheda.photofilters.model.RGB;
import com.github.rutujchheda.photofilters.util.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts an image to a grey scale version.
 */
public class GreyscaleConverter implements PhotoConverter {

    public String convert(final Photo image, final String imageName) {
        List<Pixel> pixels = new ArrayList<>();

        for (Pixel pixel : image.getPixels()) {
            RGB rgb = pixel.getRGB();
            rgb = rgb.toGreyScale();
            pixels.add(new Pixel(pixel.getX(), pixel.getY(), rgb));
        }

        Photo convertedImage = new Photo(pixels, image.getHeight(), image.getWidth(), image.getType());

        return PhotoUtil.savePhoto(convertedImage, imageName, ConversionType.GREYSCALE);
    }
}