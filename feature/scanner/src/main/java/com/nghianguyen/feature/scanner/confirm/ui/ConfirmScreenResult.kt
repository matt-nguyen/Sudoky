package com.nghianguyen.feature.scanner.confirm.ui

/** Represents the result of the confirmation screen. */
sealed interface ConfirmScreenResult {
    /** Indicates that the user wants to re-scan the Sudoku puzzle. */
    data object Redo : ConfirmScreenResult

    /**
     * Indicates that the user has confirmed the scanned puzzle and a new game has been created.
     *
     * @property gameId The unique identifier of the newly created Sudoku game.
     */
    data class ConfirmedGame(val gameId: Long) : ConfirmScreenResult

    /** Indicates that the user wants to exit the scanning process entirely. */
    data object Exit : ConfirmScreenResult
}
