package com.github.rutujchheda.photofilters.model;

public enum ConversionType {

    SEPIA("sepia"),
    GREYSCALE("greyscale"),
    INVERSION("inversion");

    private final String name;

    private ConversionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}