package com.nghianguyen.feature.sudoku.play.viewmodel

/**
 * UI actions for the Play screen.
 */
sealed interface PlayAction {
    /**
     * Triggered when a digit is entered into a specific cell.
     *
     * @param digit The digit entered (1-9).
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    data class OnDigitEntered(val digit: Int, val row: Int, val col: Int): PlayAction

    /**
     * Triggered when the user chooses to delete the current game.
     */
    data object OnDeleteGame: PlayAction
}
