package com.nghianguyen.sudoku.model

/**
 * Represents a single cell in a Sudoku grid.
 *
 * @property id Unique identifier for the cell.
 * @property state The contents of the cell and whether they are given or entered.
 * @property solution The correct value for this cell in the solved puzzle.
 * @property row The row index (0-8).
 * @property col The column index (0-8).
 */
data class DigitCell(
    val id: Long,
    val state: CellState,
    val solution: Int,
    val row: Row,
    val col: Column,
) {
    /**
     * Checks if a digit has been entered in this cell.
     *
     * @return True if [state] contains a digit.
     */
    fun hasDigitEntered(): Boolean {
        return state != CellState.Empty
    }

    /**
     * Checks if the currently entered digit matches the solution.
     *
     * @return True if the digit in [state] equals [solution].
     */
    fun isCorrect(): Boolean {
        return state.digit?.value == solution
    }
}
