package com.fitz.camerax_demo;

import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class LuminosityAnalyzer implements ImageAnalysis.Analyzer {

    private float lastAnalyzedTimestamp = 0L;



    /**
     * Analyzes an image to produce a result.
     *
     * <p>The caller is responsible for ensuring this analysis method can be executed quickly
     * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
     * images will not be acquired and analyzed.
     *
     * <p>The image passed to this method becomes invalid after this method returns. The caller
     * should not store external references to this image, as these references will become
     * invalid.
     *
     * <p>Processing should complete within a single frame time of latency, or the image data
     * should be copied out for longer processing.  Applications can be skip analyzing a frame
     * by having the analyzer return immediately.
     *
     * @param image           The image to analyze
     * @param rotationDegrees The rotation which if applied to the image would make it match
     *                        the current target rotation of {@link ImageAnalysis}, expressed as
     *                        one of {@link Surface#ROTATION_0}, {@link Surface#ROTATION_90},
     *                        {@link Surface#ROTATION_180}, or {@link Surface#ROTATION_270}.
     */
    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        float currentTimestamp = System.currentTimeMillis();
        // Calculate the average luma no more often than every second
        if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            // Extract image data from callback object
            byte[] data = buffer.array();
            // Convert the data into an array of pixel values

        }


    }



}
