package com.nghianguyen.ui.component.sudokugrid

/**
 * Calculates drawing positions and hit targets from the same square grid bounds.
 *
 * @property sideSize The side length of the grid in pixels, including its outer borders.
 */
internal class SudokuGridGeometry(val sideSize: Float) {
    private val totalLineWidth =
        (0 until SGDrawingConstants.TOTAL_LINES).sumOf { lineWidth(it).toDouble() }.toFloat()

    val cellSize = ((sideSize - totalLineWidth) / SGDrawingConstants.GRID_SIZE).coerceAtLeast(0f)
    val isDrawable = sideSize.isFinite() && cellSize > 0f

    fun lineWidth(index: Int): Float =
        if (index % SGDrawingConstants.SUB_GRID_SIZE == 0) SGDrawingConstants.THICK_LINE_WIDTH
        else SGDrawingConstants.THIN_LINE_WIDTH

    private val lineOffsets = FloatArray(SGDrawingConstants.TOTAL_LINES).apply {
        var edge = 0f
        for (index in indices) {
            this[index] = edge + lineWidth(index) / 2f
            edge += lineWidth(index) + cellSize
        }
    }

    fun lineOffset(index: Int): Float = lineOffsets[index]

    fun cellStart(index: Int): Float = lineOffset(index) + lineWidth(index) / 2f

    fun cellCenter(index: Int): Float = cellStart(index) + cellSize / 2f

    /**
     * Maps a coordinate inside the grid to a cell index, including the outer borders.
     * Internal lines select the cell on their right or below; coordinates outside return null.
     */
    fun mapCoord(coord: Float): Int? {
        if (!isDrawable || !coord.isFinite() || coord < 0f || coord > sideSize) return null
        for (index in 0 until SGDrawingConstants.GRID_SIZE - 1) {
            val end = cellStart(index) + cellSize
            if (coord < end) return index
        }
        return SGDrawingConstants.GRID_SIZE - 1
    }
}
