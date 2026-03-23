package com.nghianguyen.feature.scanner.viewmodel

import androidx.lifecycle.ViewModel
import com.nghianguyen.sudoku.model.ScannedDigitCell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel used to pass scanned Sudoku digits between different screens within the scanning
 * navigation flow (e.g., from Camera to Confirmation).
 */
class SharedScanViewModel : ViewModel() {
    private val _scannedDigits = MutableStateFlow<List<ScannedDigitCell>>(emptyList())

    /** Flow of the current list of scanned digits. */
    val scannedDigits = _scannedDigits.asStateFlow()

    /**
     * Updates the list of scanned digits.
     *
     * @param scannedDigits The new list of [ScannedDigitCell]s identified by the scanner.
     */
    fun setScannedDigits(scannedDigits: List<ScannedDigitCell>) {
        _scannedDigits.value = scannedDigits
    }
}
