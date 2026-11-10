package com.nghianguyen.sudoku.model

/** The contents of a cell and whether they belong to the original puzzle. */
sealed interface CellState {
    data class Given(val digit: Digit) : CellState

    data object Empty : CellState

    data class Entered(val digit: Digit) : CellState
}

/** The digit in this state, or null when the cell is empty. */
val CellState.digit: Digit?
    get() = when (this) {
        is CellState.Given -> digit
        CellState.Empty -> null
        is CellState.Entered -> digit
    }
