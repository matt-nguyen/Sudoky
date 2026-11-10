package com.nghianguyen.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.SudokuScanError
import com.nghianguyen.sudoku.model.ScanImage
import com.nghianguyen.sudoku.model.ScannedDigitCell

/** Interface for scanning an image of a Sudoku puzzle and identifying the digits present. */
interface SudokuScanner {
    /**
     * Scans the provided [image] for Sudoku digits.
     *
     * @param image The encoded camera image and its orientation correction.
     * @return A [Result] containing a list of [ScannedDigitCell]s if successful, or an error
     *   otherwise.
     */
    suspend fun scanForDigits(image: ScanImage): Result<List<ScannedDigitCell>, SudokuScanError>
}
