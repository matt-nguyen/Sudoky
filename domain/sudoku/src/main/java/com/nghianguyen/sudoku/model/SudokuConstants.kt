package com.nghianguyen.sudoku.model

/** The number of rows and columns in a standard Sudoku grid. */
const val GRID_SIZE = 9

/** The total number of cells in the Sudoku grid. */
const val TOTAL_CELLS = GRID_SIZE * GRID_SIZE

/** The size of the inner sub-grids (3x3). */
const val BOX_SIZE = 3

/** The minimum valid digit that can be placed in a cell. */
const val MIN_DIGIT = 1

/** The maximum valid digit that can be placed in a cell. */
const val MAX_DIGIT = 9

/** The value used to represent an empty or unsolved cell in the grid. */
const val EMPTY_CELL_VALUE = 0
