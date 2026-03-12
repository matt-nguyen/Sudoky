package com.nghianguyen.feature.home.viewmodel

/**
 * Represents the UI state of the Home screen.
 * @property gameInProgress True if there is a Sudoku game currently in progress that can be continued.
 */
data class HomeScreenState(
    val gameInProgress: Boolean
)
