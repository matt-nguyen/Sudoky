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
     * @return True if all 81 positions are present and every cell matches its solution value.
     */
    fun isFinished(): Boolean {
        return cells.size == TOTAL_CELLS && cells.all { it.isCorrect() }
    }

    /** Enters an answer or clears an existing answer when [digit] is zero. */
    fun setDigit(digit: Int, row: Int, col: Int): Result<SudokuGame, SudokuGameError> =
        editCell(digit, row, col) { target ->
            if (digit == EMPTY_CELL_VALUE) {
                if (target.state == CellState.Empty) return@editCell this
                return@editCell copy(
                    cells =
                        cells.map { cell ->
                            if (cell === target) cell.copy(state = CellState.Empty) else cell
                        },
                )
            }

            val answer = Digit(digit)
            copy(
                cells =
                    cells.map { cell ->
                        when {
                            cell === target ->
                                cell.copy(state = CellState.Entered(answer), notes = emptySet())
                            digit == target.solution &&
                                answer in cell.notes &&
                                cell.isPeerOf(target) -> cell.copy(notes = cell.notes - answer)
                            else -> cell
                        }
                    },
            )
        }

    /** Toggles a candidate digit, or clears all notes when [digit] is zero. */
    fun editNotes(digit: Int, row: Int, col: Int): Result<SudokuGame, SudokuGameError> =
        editCell(digit, row, col) { target ->
            if (target.state != CellState.Empty) return@editCell this

            val notes =
                if (digit == EMPTY_CELL_VALUE) {
                    emptySet()
                } else {
                    val note = Digit(digit)
                    if (note in target.notes) target.notes - note else target.notes + note
                }
            copy(
                cells =
                    cells.map { cell -> if (cell === target) cell.copy(notes = notes) else cell },
            )
        }

    private fun editCell(
        digit: Int,
        row: Int,
        col: Int,
        transform: (DigitCell) -> SudokuGame,
    ): Result<SudokuGame, SudokuGameError> {
        if (digit != EMPTY_CELL_VALUE && digit !in MIN_DIGIT..MAX_DIGIT) {
            return Err(SudokuGameError.InvalidDigit(digit))
        }
        val target =
            cells.find { it.row.value == row && it.col.value == col }
                ?: return Err(SudokuGameError.InvalidCoordinate(row, col))
        if (target.state is CellState.Given) {
            return Err(SudokuGameError.GivenCellModification(row, col))
        }

        return Ok(transform(target))
    }

    private fun DigitCell.isPeerOf(target: DigitCell): Boolean =
        row == target.row ||
            col == target.col ||
            (row.value / BOX_SIZE == target.row.value / BOX_SIZE &&
                col.value / BOX_SIZE == target.col.value / BOX_SIZE)
}
