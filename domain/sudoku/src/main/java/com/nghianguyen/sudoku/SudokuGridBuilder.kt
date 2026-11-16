package com.nghianguyen.sudoku

import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Column
import com.nghianguyen.sudoku.model.Digit
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.sudoku.model.GRID_SIZE
import com.nghianguyen.sudoku.model.MAX_DIGIT
import com.nghianguyen.sudoku.model.MIN_DIGIT
import com.nghianguyen.sudoku.model.Row
import com.nghianguyen.sudoku.model.ScannedDigitCell

/** Builds Sudoku grids from scanned digits. */
class SudokuGridBuilder {

    /**
     * Converts a list of [ScannedDigitCell]s into a full 9x9 grid of [DigitCell]s. Cells that were
     * not present in the [scannedDigits] list will be initialized as empty (digit 0).
     *
     * @param scannedDigits The list of digits detected by the scanner.
     * @return A list of 81 [DigitCell]s representing the full Sudoku grid.
     */
    fun buildSudokuGrid(scannedDigits: List<ScannedDigitCell>): List<DigitCell> {
        val digits = mutableListOf<DigitCell>()

        // Adding given cells
        val scanned = Array(GRID_SIZE) { IntArray(GRID_SIZE) }
        for (scannedDigit in scannedDigits) {
            val digit = scannedDigit.digit.toIntOrNull()
            scanned[scannedDigit.row][scannedDigit.col] =
                digit?.takeIf { it in MIN_DIGIT..MAX_DIGIT } ?: EMPTY_CELL_VALUE
        }

        // Filling in the rest with 0s
        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                val digit = scanned[row][col]
                digits.add(
                    DigitCell(
                        id = 0,
                        state =
                            if (digit == EMPTY_CELL_VALUE) CellState.Empty
                            else CellState.Given(Digit(digit)),
                        solution = 0,
                        row = Row(row),
                        col = Column(col),
                    )
                )
            }
        }

        return digits
    }
}
