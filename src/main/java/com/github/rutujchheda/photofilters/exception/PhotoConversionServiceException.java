package com.github.rutujchheda.photofilters.exception;


/**
 * Exception thrown by the Photo Conversion Service when an error occurs due to a service error.
 */
public class PhotoConversionServiceException extends RuntimeException {

    private static final long serialVersionUID = 550398551328396840L;

    public PhotoConversionServiceException(String message) {
        super(message);
    }

    public PhotoConversionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}