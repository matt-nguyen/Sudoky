package com.nghianguyen.data.scanner

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Interface for preprocessing images before they are passed to the Sudoku scanner. This typically
 * involves cropping the image to a square format, scaling it to a standard size, and applying
 * necessary rotations.
 */
interface ImagePreprocessor {
    /**
     * Processes the [source] bitmap by cropping it to a square, scaling it to [targetSizePx], and
     * rotating it by [rotationDegrees].
     *
     * @param source The source bitmap to process.
     * @param rotationDegrees The degrees to rotate the bitmap (e.g., to correct orientation).
     * @param targetSizePx The target size (width and height) of the resulting square bitmap in
     *   pixels.
     * @return A new square [Bitmap] that has been cropped, scaled, and rotated.
     */
    fun process(source: Bitmap, rotationDegrees: Float, targetSizePx: Float): Bitmap
}

class ImagePreprocessorImpl : ImagePreprocessor {

    override fun process(source: Bitmap, rotationDegrees: Float, targetSizePx: Float): Bitmap {
        // Using source.height b/c the incoming source rotated and in landscape mode
        // Will eventually be cropped, scaled, then rotated.
        // TODO test on landscape mode
        val scale = targetSizePx / source.height
        val transformationMatrix =
            Matrix().apply {
                postScale(scale, scale)
                postRotate(rotationDegrees)
            }

        // Starting point to crop image, adjusting ratio from 4:3 to 1:1(square)
        // Cropping enough width from the top to match height in the end result
        val cropX = (source.width - source.height) / 2

        return Bitmap.createBitmap(
            /* source = */ source,
            /* x = */ cropX,
            /* y = */ 0,
            /* width = */ source.height,
            /* height = */ source.height,
            /* m = */ transformationMatrix,
            /* filter = */ true,
        )
    }
}
