package com.nghianguyen.feature.sudoku.play.ui

/** Represents the result of the Play screen. */
sealed interface PlayScreenResult {
    /** Indicates that the user has requested to exit the Play screen. */
    data object Exit : PlayScreenResult
}
