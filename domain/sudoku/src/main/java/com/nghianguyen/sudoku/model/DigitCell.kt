package com.nghianguyen.sudoku.model

/**
 * Represents a single cell in a Sudoku grid.
 *
 * @property id Unique identifier for the cell.
 * @property current The current value entered in the cell (0 if empty).
 * @property solution The correct value for this cell in the solved puzzle.
 * @property row The row index (0-8).
 * @property col The column index (0-8).
 * @property isGiven True if the digit was part of the initial puzzle state and cannot be changed.
 */
data class DigitCell(
    val id: Long,
    val current: Int,
    val solution: Int,
    val row: Int,
    val col: Int,
    val isGiven: Boolean
) {
    /**
     * Checks if a digit has been entered in this cell.
     * @return True if [current] is not 0.
     */
    fun hasDigitEntered(): Boolean {
        return current != 0
    }

    /**
     * Checks if the currently entered digit matches the solution.
     * @return True if [current] equals [solution].
     */
    fun isCorrect(): Boolean {
        return current == solution
    }
}
