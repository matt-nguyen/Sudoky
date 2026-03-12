package com.nghianguyen.sudoku

import android.graphics.Bitmap
import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.SudokuScanError
import com.nghianguyen.sudoku.model.ScannedDigitCell

/**
 * Interface for scanning an image of a Sudoku puzzle and identifying the digits present.
 */
interface SudokuScanner {
    /**
     * Scans the provided [bitmap] for Sudoku digits.
     *
     * @param bitmap The image to scan. It should be a square image containing a Sudoku grid.
     * @return A [Result] containing a list of [ScannedDigitCell]s if successful, or an error otherwise.
     */
    suspend fun scanForDigits(bitmap: Bitmap): Result<List<ScannedDigitCell>, SudokuScanError>
}
