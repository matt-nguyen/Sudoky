package com.nghianguyen.feature.home.ui

/** Represents the result of the home screen. */
sealed interface HomeScreenResult {
    /** Indicates that the user wants to start a new Sudoku scan. */
    data object ScanSudoku : HomeScreenResult

    /**
     * Indicates that the user wants to continue a game with the specified [gameId].
     *
     * @property gameId The unique identifier of the game to continue.
     */
    data class ContinueGame(val gameId: Long) : HomeScreenResult
}
