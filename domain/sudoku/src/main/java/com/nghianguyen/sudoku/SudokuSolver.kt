package com.nghianguyen.sudoku

import android.util.Log
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.ScannedDigitCell

/**
 * Provides logic for processing scanned Sudoku digits and solving Sudoku puzzles.
 */
class SudokuSolver {

    /**
     * Converts a list of [ScannedDigitCell]s into a full 9x9 grid of [DigitCell]s.
     * Cells that were not present in the [scannedDigits] list will be initialized as empty (digit 0).
     *
     * @param scannedDigits The list of digits detected by the scanner.
     * @return A list of 81 [DigitCell]s representing the full Sudoku grid.
     */
    // TODO extract this method out of SudokuSolver
    fun buildSudokuGrid(scannedDigits: List<ScannedDigitCell>): List<DigitCell> {
        Log.d("SudokuSolver", "buildSudokuGrid")
        val digits = mutableListOf<DigitCell>()

        // Adding given cells
        val scanned = Array(9) { IntArray(9) }
        for (scannedDigit in scannedDigits) {
            val digit = scannedDigit.digit.toIntOrNull()
            if (digit == null) {
                Log.d("SudokuSolver", "${scannedDigit.digit} is not a digit, on row ${scannedDigit.row}, col ${scannedDigit.col}")
            }
            scanned[scannedDigit.row][scannedDigit.col] = digit ?: 0
        }

        // Filling in the rest with 0s
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val digit = scanned[row][col]
                digits.add(
                    DigitCell(
                        id = 0,
                        current = digit,
                        solution = 0,
                        row = row,
                        col = col,
                        isGiven = digit > 0
                    )
                )
            }
        }

        return digits
    }

    /**
     * Attempts to solve the provided Sudoku board.
     * Returns a [Result] containing the list of [DigitCell]s with their [DigitCell.solution] 
     * property filled if the board is solvable.
     *
     * @param cells The list of 81 cells representing the current state of the board.
     * @return A [Result] containing the solved list of cells, or a failure if the puzzle is unsolvable.
     */
    fun solveBoard(cells: List<DigitCell>): Result<List<DigitCell>> {
        return runCatching {
            require(cells.size == 81) { "Board must contain 81 cells" }

            // Convert to 2D grid
            val board = Array(9) { IntArray(9) }
            cells.forEach { cell ->
                board[cell.row][cell.col] = cell.current
            }

            // Solve the puzzle
            val solved = solve(board)
            if (!solved) {
                throw RuntimeException("Sudoku puzzle is unsolvable")
            }

            // Return updated list with solutionDigit filled
            cells.map { cell ->
                cell.copy(solution = board[cell.row][cell.col])
            }
        }
    }

    /**
     * Attempts to solve the provided Sudoku [board].
     *
     * @param board The 2D array representing the Sudoku board.
     * @return True if the puzzle was solved.
     */
    private fun solve(board: Array<IntArray>): Boolean {

        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    /**
     * Validates if placing a [num] at [row], [col] is legal according to Sudoku rules.
     *
     * @param board The current state of the board.
     * @param row The row index.
     * @param col The column index.
     * @param num The number to check.
     * @return True if the placement is valid.
     */
    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Row check
        for (c in 0 until 9) {
            if (board[row][c] == num) return false
        }
        // Column check
        for (r in 0 until 9) {
            if (board[r][col] == num) return false
        }
        // 3x3 Box check
        val boxRowStart = row - row % 3
        val boxColStart = col - col % 3
        for (r in boxRowStart until boxRowStart + 3) {
            for (c in boxColStart until boxColStart + 3) {
                if (board[r][c] == num) return false
            }
        }
        return true
    }
}
