package com.nghianguyen.feature.sudoku.play.viewmodel

import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState

/**
 * State of the Play screen.
 *
 * @param digits The current digits of the Sudoku board.
 * @param sudokuGridState The state of the Sudoku grid UI component.
 */
data class PlayScreenState(
    val digits: List<DigitCell>,
    val sudokuGridState: SudokuGridState = SudokuGridState()
)
