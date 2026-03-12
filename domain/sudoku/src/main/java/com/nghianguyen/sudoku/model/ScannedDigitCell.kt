package com.nghianguyen.sudoku.model

/**
 * Represents a digit identified by the Sudoku scanner at a specific grid position.
 *
 * @property digit The string representation of the identified digit.
 * @property row The row index where the digit was found (0-8).
 * @property col The column index where the digit was found (0-8).
 */
data class ScannedDigitCell(val digit: String, val row: Int, val col: Int)
