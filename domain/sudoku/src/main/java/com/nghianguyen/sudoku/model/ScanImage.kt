package com.nghianguyen.sudoku.model

/**
 * Represents an encoded image of a Sudoku puzzle.
 *
 * The producer must not mutate [encodedBytes] after handing the image to the scanner.
 *
 * @property encodedBytes The JPEG-encoded image data.
 * @property rotationDegrees The clockwise rotation needed to make the image upright.
 */
data class ScanImage(val encodedBytes: ByteArray, val rotationDegrees: Int)
