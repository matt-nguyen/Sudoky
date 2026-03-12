package com.nghianguyen.feature.scanner.confirm.viewmodel

/**
 * Represents events dispatched from the confirmation ViewModel.
 */
sealed interface ConfirmEvent {
    /**
     * Dispatched when a new game has been successfully created from the confirmed scan.
     * @property gameId The ID of the newly created game.
     */
    data class ConfirmedGame(val gameId: Long): ConfirmEvent
}