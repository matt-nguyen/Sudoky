package com.nghianguyen.feature.home.viewmodel

/**
 * Represents events dispatched from the Home ViewModel.
 */
sealed interface HomeEvent {
    /**
     * Event to navigate the user to the Scanner screen.
     */
    data object NavigateToScanner: HomeEvent

    /**
     * Event to navigate the user to the Play screen for a specific game.
     * @property gameId The ID of the game to be played.
     */
    data class NavigateToPlay(val gameId: Long): HomeEvent
}
