package com.nghianguyen.feature.sudoku.play.viewmodel

/**
 * Events that occur on the Play screen.
 */
sealed interface PlayEvent {
    /**
     * Triggered when the current Sudoku game is finished.
     */
    data object GameFinished: PlayEvent

    /**
     * Triggered when the current Sudoku game is successfully deleted.
     */
    data object GameDeleted: PlayEvent
}
