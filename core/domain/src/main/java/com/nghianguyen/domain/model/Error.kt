package com.nghianguyen.domain.model

/** Base interface for all error types in the application. */
sealed interface Error {}

/** Represents errors related to local data storage and retrieval. */
enum class LocalDataError : Error {
    /** Indicates a generic database error occurred. */
    DATABASE_ERROR
}

enum class SudokuScanError : Error {
    SCAN_FAILED
}

/** Represents errors that can occur during Sudoku game logic execution. */
sealed interface SudokuGameError : Error {
    /**
     * Indicates an invalid coordinate (row or column) was provided.
     *
     * @property row The invalid row index.
     * @property col The invalid column index.
     */
    data class InvalidCoordinate(val row: Int, val col: Int) : SudokuGameError

    /**
     * Indicates an invalid digit (outside the 1-9 range) was provided.
     *
     * @property digit The invalid digit value.
     */
    data class InvalidDigit(val digit: Int) : SudokuGameError

    /**
     * Indicates an attempt to modify a cell that was part of the original puzzle state.
     *
     * @property row The row index of the given cell.
     * @property col The column index of the given cell.
     */
    data class GivenCellModification(val row: Int, val col: Int) : SudokuGameError
}
