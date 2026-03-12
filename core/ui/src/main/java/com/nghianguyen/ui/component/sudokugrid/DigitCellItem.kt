package com.nghianguyen.ui.component.sudokugrid

/**
 * Represents a digit to be displayed in a Sudoku grid cell.
 *
 * @property digit The numeric value of the digit (1-9).
 * @property row The row index of the cell (0-8).
 * @property col The column index of the cell (0-8).
 */
data class DigitCellItem(val digit: Int, val row: Int, val col: Int)
