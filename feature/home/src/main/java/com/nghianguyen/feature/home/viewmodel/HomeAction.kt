package com.nghianguyen.feature.home.viewmodel

/**
 * Represents the user actions that can be performed on the Home screen.
 */
sealed interface HomeAction {
    /**
     * Triggered when the user clicks the "Scan Puzzle" button.
     */
    data object OnScanClick: HomeAction

    /**
     * Triggered when the user clicks the "Continue Game" button.
     */
    data object OnContinueClick: HomeAction
}
