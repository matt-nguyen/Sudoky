package com.nghianguyen.feature.scanner.confirm.viewmodel

import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState

/**
 * Represents the UI state of the confirmation screen.
 *
 * @property digits The full list of 81 digits representing the scanned Sudoku board.
 * @property sudokuGridState The state of the [SudokuGrid] component, used for rendering.
 * @property isValid True if the scanned board is a valid (solvable) Sudoku puzzle.
 */
data class ConfirmScreenState(
    val digits: List<DigitCell>,
    val sudokuGridState: SudokuGridState,
    val isValid: Boolean
)
