package com.github.rutujchheda.photofilters.converter;

import com.github.rutujchheda.photofilters.model.Photo;

/**
 * Strategy interface for converting images.
 */
public interface PhotoConverter {
    String convert(Photo image, String imageName);
}