package com.nghianguyen.ui.component.sudokugrid

import androidx.compose.ui.unit.sp

/**
 * Constants used for drawing the Sudoku grid UI component.
 */
internal object SGDrawingConstants {
    /** The number of cells in a single row or column of the Sudoku grid. */
    const val GRID_SIZE = 9

    /** The number of cells in a single row or column of a sub-grid. */
    const val SUB_GRID_SIZE = 3

    /** The width of the thick lines separating sub-grids. */
    const val THICK_LINE_WIDTH = 8f

    /** The width of the thin lines separating individual cells within a sub-grid. */
    const val THIN_LINE_WIDTH = 2f

    /** The font size used for the digits displayed in the cells. */
    val FONT_SIZE = 24.sp

    /** The width of the selection border stroke. */
    const val SELECTION_STROKE_WIDTH = 8f

    /** The offset of the selection border from the cell boundary. */
    const val SELECTION_OFFSET = 4f

    /** The total reduction in size for the selection border relative to the cell size. */
    const val SELECTION_SIZE_REDUCTION = 8f

    /** The total number of thick grid lines, including the outer borders. */
    const val NUM_THICK_LINES = (GRID_SIZE / SUB_GRID_SIZE) + 1

    /** The total number of thin grid lines. */
    const val NUM_THIN_LINES = (GRID_SIZE + 1) - NUM_THICK_LINES

    /** The total number of grid lines in each direction. */
    const val TOTAL_LINES = GRID_SIZE + 1
}
