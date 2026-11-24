package com.nghianguyen.feature.scanner.confirm.viewmodel

import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItems

/**
 * Represents the UI state of the confirmation screen.
 *
 * @property digits The full list of 81 digits representing the scanned Sudoku board.
 * @property validationStatus The validation result for the current puzzle.
 */
data class ConfirmScreenState(
    val digits: List<DigitCell>,
    val validationStatus: ValidationStatus = ValidationStatus.Unchecked,
    val isEditing: Boolean = false,
    val selectedCell: Pair<Int, Int>? = null,
    val warning: String? = null,
    val isSubmitting: Boolean = false,
) {
    /** Grid rendering derived from the current puzzle cells. */
    val sudokuGridState: SudokuGridState
        get() =
            SudokuGridState(
                givenItems = digits.filter { it.state is CellState.Given }.toDigitCellItems()
            )
}
