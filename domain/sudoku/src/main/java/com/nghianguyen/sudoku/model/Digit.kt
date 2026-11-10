package com.nghianguyen.sudoku.model

/** A Sudoku digit in the range 1-9. */
@JvmInline
value class Digit(val value: Int) {
    init {
        require(value in MIN_DIGIT..MAX_DIGIT) { "Digit must be between $MIN_DIGIT and $MAX_DIGIT" }
    }
}
