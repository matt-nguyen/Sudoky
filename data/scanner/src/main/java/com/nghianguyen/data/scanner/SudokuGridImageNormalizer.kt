package com.nghianguyen.data.scanner

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Interface for normalizing images before text recognition. This typically
 * involves cropping the image to a square format, scaling it to a standard size, and applying
 * necessary rotations.
 */
interface SudokuGridImageNormalizer {
    /**
     * Normalizes the [source] bitmap by cropping it to a centered square, scaling it to [targetSizePx], and
     * rotating it by [rotationDegrees].
     *
     * @param source The source bitmap to process.
     * @param rotationDegrees The degrees to rotate the bitmap (e.g., to correct orientation).
     * @param targetSizePx The target size (width and height) of the resulting square bitmap in
     *   pixels.
     * @return A square [Bitmap] that has been cropped, scaled, and rotated.
     */
    fun normalize(source: Bitmap, rotationDegrees: Float, targetSizePx: Float): Bitmap
}

/**
 * Default implementation of [SudokuGridImageNormalizer] that uses [Matrix] transformations to crop,
 * scale, and rotate bitmaps.
 */
class SudokuGridImageNormalizerImpl : SudokuGridImageNormalizer {

    override fun normalize(source: Bitmap, rotationDegrees: Float, targetSizePx: Float): Bitmap {
        val side = minOf(source.width, source.height)
        val scale = targetSizePx / side
        val transformationMatrix =
            Matrix().apply {
                postScale(scale, scale)
                postRotate(rotationDegrees)
            }
        val cropX = (source.width - side) / 2
        val cropY = (source.height - side) / 2

        return Bitmap.createBitmap(
            /* source = */ source,
            /* x = */ cropX,
            /* y = */ cropY,
            /* width = */ side,
            /* height = */ side,
            /* m = */ transformationMatrix,
            /* filter = */ true,
        )
    }
}
