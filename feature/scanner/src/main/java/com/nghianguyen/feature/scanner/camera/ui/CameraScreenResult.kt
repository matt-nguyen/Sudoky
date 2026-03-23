package com.nghianguyen.feature.scanner.camera.ui

import com.nghianguyen.sudoku.model.ScannedDigitCell

/** Represents the result of the camera preview screen. */
sealed interface CameraScreenResult {
    /**
     * Indicates that the camera has successfully scanned digits from a Sudoku grid.
     *
     * @property digits The list of scanned digits and their positions.
     */
    data class ScannedDigits(val digits: List<ScannedDigitCell>) : CameraScreenResult

    /** Indicates that the user has exited the camera preview without scanning. */
    data object Exit : CameraScreenResult
}
