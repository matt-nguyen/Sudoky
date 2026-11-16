package com.nghianguyen.feature.sudoku.play.viewmodel

import com.nghianguyen.ui.component.sudokugrid.SudokuGridState

/**
 * State of the Play screen.
 *
 * @param sudokuGridState The state of the Sudoku grid UI component.
 * @param showFinishedDialog Whether the completion dialog is visible.
 */
data class PlayScreenState(
    val sudokuGridState: SudokuGridState = SudokuGridState(),
    val showFinishedDialog: Boolean = false,
)
