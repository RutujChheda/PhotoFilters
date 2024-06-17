package com.github.rutujchheda.photofilters.tester;


import com.github.rutujchheda.photofilters.activity.ConvertPhotoActivity;
import com.github.rutujchheda.photofilters.dependency.DaggerServiceComponent;
import com.github.rutujchheda.photofilters.dependency.ServiceComponent;
import com.github.rutujchheda.photofilters.model.ConversionType;
import com.google.common.collect.ImmutableList;

/**
 * A class provided for interacting with the PhotoConverterService
 */
public class PhotoConverterManualTester {

    private static final ServiceComponent DAGGER = DaggerServiceComponent.create();

    /**
     * If you're having issues running the main method, check the "Before starting" steps in the README.
     */
    public static void main(String[] args) {
        runTest("src/main/resources/Photographing-NYC-skyline.jpg", ImmutableList.of(ConversionType.INVERSION,
                ConversionType.GREYSCALE, ConversionType.SEPIA));

        // Uncomment the below line to run a test that converts an image to a single filter type.
//         runTest("src/main/resources/Photographing-NYC-skyline.jpg", ImmutableList.of(ConversionType.SEPIA));
    }

    private static void runTest(String filePath, ImmutableList<ConversionType> conversions) {
        ConvertPhotoActivity activity = DAGGER.provideConvertPhotoActivity();
        activity.handleRequest(filePath, conversions);
    }
}