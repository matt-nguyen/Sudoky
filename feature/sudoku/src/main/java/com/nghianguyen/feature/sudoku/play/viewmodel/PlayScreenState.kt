package com.nghianguyen.feature.sudoku.play.viewmodel

import com.nghianguyen.ui.component.sudokugrid.SudokuGridState

/**
 * State of the Play screen.
 *
 * @param sudokuGridState The state of the Sudoku grid UI component.
 * @param showFinishedDialog Whether the completion dialog is visible.
 * @param selectedCell Zero-based coordinates of the selected cell.
 * @param notesMode Whether the keypad enters notes.
 */
data class PlayScreenState(
    val sudokuGridState: SudokuGridState = SudokuGridState(),
    val showFinishedDialog: Boolean = false,
    val selectedCell: Pair<Int, Int>? = null,
    val notesMode: Boolean = false,
)
