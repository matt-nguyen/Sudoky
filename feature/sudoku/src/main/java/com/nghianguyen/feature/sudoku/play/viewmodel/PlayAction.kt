package com.nghianguyen.feature.sudoku.play.viewmodel

/** UI actions for the Play screen. */
sealed interface PlayAction {
    /** Applies a keypad digit or Clear to the current selection and mode. */
    data class OnDigitEntered(val digit: Int) : PlayAction

    /** Updates the selected position in the grid. */
    data class OnCellSelected(val row: Int, val col: Int) : PlayAction

    /** Changes whether the keypad enters notes. */
    data object OnNotesModeToggled : PlayAction

    /** Triggered when the user chooses to delete the current game. */
    data object OnDeleteGame : PlayAction
}
