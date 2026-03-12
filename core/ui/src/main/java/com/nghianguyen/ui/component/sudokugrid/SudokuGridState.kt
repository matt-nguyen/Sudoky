package com.nghianguyen.ui.component.sudokugrid

/**
 * Represents the visual state of a Sudoku grid.
 *
 * @property correctItems List of digits correctly entered by the user.
 * @property incorrectItems List of digits incorrectly entered by the user.
 * @property givenItems List of digits that were part of the original Sudoku puzzle.
 */
data class SudokuGridState(
    val correctItems: List<DigitCellItem> = emptyList(),
    val incorrectItems: List<DigitCellItem> = emptyList(),
    val givenItems: List<DigitCellItem> = emptyList(),
)
