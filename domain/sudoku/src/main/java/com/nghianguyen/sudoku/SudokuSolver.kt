package com.nghianguyen.sudoku

import com.nghianguyen.sudoku.model.BOX_SIZE
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.sudoku.model.GRID_SIZE
import com.nghianguyen.sudoku.model.MAX_DIGIT
import com.nghianguyen.sudoku.model.MIN_DIGIT
import com.nghianguyen.sudoku.model.TOTAL_CELLS
import com.nghianguyen.sudoku.model.digit

/** Provides logic for solving Sudoku puzzles. */
class SudokuSolver {

    /**
     * Attempts to solve the provided Sudoku board. Returns a [Result] containing the list of
     * [DigitCell]s with their [DigitCell.solution] property filled if the board is solvable.
     *
     * @param cells The list of 81 cells representing the current state of the board.
     * @return A [Result] containing the solved list of cells, or a failure if the puzzle is
     *   unsolvable.
     */
    fun solveBoard(cells: List<DigitCell>): Result<List<DigitCell>> {
        return runCatching {
            require(cells.size == TOTAL_CELLS) { "Board must contain $TOTAL_CELLS cells" }

            // Convert to 2D grid
            val board = Array(GRID_SIZE) { IntArray(GRID_SIZE) }
            cells.forEach { cell ->
                board[cell.row.value][cell.col.value] = cell.state.digit?.value ?: EMPTY_CELL_VALUE
            }

            // Solve the puzzle
            val solved = solve(board)
            if (!solved) {
                throw RuntimeException("Sudoku puzzle is unsolvable")
            }

            // Return updated list with solutionDigit filled
            cells.map { cell -> cell.copy(solution = board[cell.row.value][cell.col.value]) }
        }
    }

    /**
     * Attempts to solve the provided Sudoku [board].
     *
     * @param board The 2D array representing the Sudoku board.
     * @return True if the puzzle was solved.
     */
    private fun solve(board: Array<IntArray>): Boolean {

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                if (board[row][col] == EMPTY_CELL_VALUE) {
                    for (num in MIN_DIGIT..MAX_DIGIT) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = EMPTY_CELL_VALUE
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
        for (c in 0 until GRID_SIZE) {
            if (board[row][c] == num) return false
        }
        // Column check
        for (r in 0 until GRID_SIZE) {
            if (board[r][col] == num) return false
        }
        // 3x3 Box check
        val boxRowStart = row - row % BOX_SIZE
        val boxColStart = col - col % BOX_SIZE
        for (r in boxRowStart until boxRowStart + BOX_SIZE) {
            for (c in boxColStart until boxColStart + BOX_SIZE) {
                if (board[r][c] == num) return false
            }
        }
        return true
    }
}
