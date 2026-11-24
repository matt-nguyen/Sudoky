package com.nghianguyen.feature.scanner.confirm.viewmodel

/** Represents the user actions that can be performed on the confirmation screen. */
sealed interface ConfirmAction {
    data object Modify : ConfirmAction

    data object Back : ConfirmAction

    data class SelectCell(val row: Int, val col: Int) : ConfirmAction

    data class EnterDigit(val digit: Int) : ConfirmAction

    /** Triggered when the user confirms the scanned Sudoku puzzle. */
    data object Confirm : ConfirmAction
}
