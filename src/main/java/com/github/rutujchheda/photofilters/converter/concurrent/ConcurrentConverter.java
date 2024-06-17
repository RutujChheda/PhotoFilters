package com.github.rutujchheda.photofilters.converter.concurrent;

import com.github.rutujchheda.photofilters.converter.PhotoConverter;
import com.github.rutujchheda.photofilters.model.Photo;

/**
 * Concurrently converts an image using a PhotoConverter strategy.
 */
public class ConcurrentConverter implements Runnable {
    private final PhotoConverter converter;
    private final Photo inputImage;
    private final String fileName;
    private String convertedImageLocation;

    public ConcurrentConverter(final PhotoConverter converter, final Photo inputImage, final String fileName) {
        this.converter = converter;
        this.inputImage = inputImage;
        this.fileName = fileName;
    }

    public String getConvertedImageLocation() {
        if (convertedImageLocation == null) {
            throw new IllegalStateException("Conversion computation not complete yet, or did not succeed.");
        }
        return convertedImageLocation;
    }

    @Override
    public void run() {
        // Calls one of the strategies that implements PhotoConverter
        convertedImageLocation = converter.convert(inputImage, fileName);
    }
}