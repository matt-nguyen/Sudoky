package com.nghianguyen.feature.scanner.camera.viewmodel

import com.nghianguyen.sudoku.model.ScannedDigitCell

/**
 * Represents events dispatched from the camera scanning ViewModel.
 */
sealed interface CameraEvent {
    /**
     * Dispatched when digits have been successfully identified in the captured image.
     * @property scannedDigits The list of identified digits and their positions.
     */
    data class DigitsScanned(val scannedDigits: List<ScannedDigitCell>): CameraEvent
}
