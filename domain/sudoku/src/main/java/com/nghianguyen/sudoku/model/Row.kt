package com.nghianguyen.sudoku.model

/** A zero-based Sudoku row index in the range 0-8. */
@JvmInline
value class Row(val value: Int) {
    init {
        require(value in 0 until GRID_SIZE) { "Row must be between 0 and ${GRID_SIZE - 1}" }
    }
}
