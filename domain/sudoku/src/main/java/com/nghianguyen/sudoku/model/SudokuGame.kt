package com.nghianguyen.sudoku.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.SudokuGameError

/**
 * Represents a Sudoku game session.
 *
 * @property id The unique identifier for this game.
 * @property cells The list of [DigitCell]s making up the 9x9 grid.
 */
data class SudokuGame(val id: Long, val cells: List<DigitCell>) {

    /**
     * Checks if the game is successfully finished.
     *
     * @return True if every cell in the grid matches its solution value.
     */
    fun isFinished(): Boolean {
        return cells.all { it.isCorrect() }
    }

    /**
     * Updates the value of a specific cell in the grid.
     *
     * @param digit The new digit to set (1-9).
     * @param row The row index (0-8).
     * @param col The column index (0-8).
     * @return An [Ok] result with the updated [SudokuGame] or an [Err] if the operation is invalid.
     */
    fun setDigit(digit: Int, row: Int, col: Int): Result<SudokuGame, SudokuGameError> {
        if (digit !in 1..9) {
            return Err(SudokuGameError.InvalidDigit(digit))
        }
        if (row !in 0..8 || col !in 0..8) {
            return Err(SudokuGameError.InvalidCoordinate(row, col))
        }

        val index = cells.indexOfFirst { it.row == row && it.col == col }
        val targetCell =
            cells.getOrNull(index) ?: return Err(SudokuGameError.InvalidCoordinate(row, col))

        if (targetCell.isGiven) {
            return Err(SudokuGameError.GivenCellModification(row, col))
        }

        val updatedCells =
            cells.toMutableList().apply { set(index, targetCell.copy(current = digit)) }

        return Ok(copy(cells = updatedCells))
    }
}
