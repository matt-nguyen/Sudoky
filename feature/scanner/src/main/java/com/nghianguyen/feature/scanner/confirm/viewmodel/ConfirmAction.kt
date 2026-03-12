package com.nghianguyen.feature.scanner.confirm.viewmodel

/**
 * Represents the user actions that can be performed on the confirmation screen.
 */
sealed interface ConfirmAction {
    /**
     * Triggered when the user confirms the scanned Sudoku puzzle.
     */
    data object Confirm: ConfirmAction
}
