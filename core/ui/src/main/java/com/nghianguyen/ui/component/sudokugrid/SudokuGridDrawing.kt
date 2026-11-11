package com.nghianguyen.ui.component.sudokugrid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText

/** Draws cell backgrounds, grid lines, digits, and selection in that order. */
internal fun DrawScope.drawSudokuGrid(
    state: SudokuGridState,
    geometry: SudokuGridGeometry,
    style: SudokuGridStyle,
    selectedCell: Pair<Int, Int>?,
    textMeasurer: TextMeasurer,
) {
    if (!geometry.isDrawable) return
    drawIncorrectBackgrounds(
        items = state.incorrectItems,
        geometry = geometry,
        color = style.wrongBackgroundColor,
    )
    drawGridLines(geometry, style.gridColor)
    drawDigits(
        items = state.givenItems,
        geometry = geometry,
        style = style.givenDigitTextStyle,
        textMeasurer = textMeasurer,
    )
    drawDigits(
        items = state.correctItems,
        geometry = geometry,
        style = style.enteredDigitTextStyle,
        textMeasurer = textMeasurer,
    )
    drawDigits(
        items = state.incorrectItems,
        geometry = geometry,
        style = style.wrongDigitTextStyle,
        textMeasurer = textMeasurer,
    )
    selectedCell?.let { (row, col) ->
        drawSelectedCell(
            row = row,
            col = col,
            geometry = geometry,
            color = style.selectionColor,
        )
    }
}

private fun DrawScope.drawIncorrectBackgrounds(
    items: List<DigitCellItem>,
    geometry: SudokuGridGeometry,
    color: Color,
) {
    items.forEach { (_, row, col) ->
        drawRect(
            color = color,
            topLeft = Offset(geometry.cellStart(col), geometry.cellStart(row)),
            size = Size(geometry.cellSize, geometry.cellSize),
        )
    }
}

private fun DrawScope.drawGridLines(geometry: SudokuGridGeometry, color: Color) {
    for (index in 0 until SGDrawingConstants.TOTAL_LINES) {
        val offset = geometry.lineOffset(index)
        val width = geometry.lineWidth(index)
        drawLine(
            color = color,
            start = Offset(0f, offset),
            end = Offset(geometry.sideSize, offset),
            strokeWidth = width,
        )
        drawLine(
            color = color,
            start = Offset(offset, 0f),
            end = Offset(offset, geometry.sideSize),
            strokeWidth = width,
        )
    }
}

private fun DrawScope.drawDigits(
    items: List<DigitCellItem>,
    geometry: SudokuGridGeometry,
    style: TextStyle,
    textMeasurer: TextMeasurer,
) {
    items.forEach { (digit, row, col) ->
        val result = textMeasurer.measure(digit.toString(), style)
        drawText(
            textLayoutResult = result,
            topLeft =
                Offset(
                    geometry.cellCenter(col) - result.size.width / 2f,
                    geometry.cellCenter(row) - result.size.height / 2f,
                ),
        )
    }
}

private fun DrawScope.drawSelectedCell(
    row: Int,
    col: Int,
    geometry: SudokuGridGeometry,
    color: Color,
) {
    val side = geometry.cellSize - SGDrawingConstants.SELECTION_SIZE_REDUCTION
    if (side <= 0f) return
    drawRect(
        color = color,
        topLeft =
            Offset(
                geometry.cellStart(col) + SGDrawingConstants.SELECTION_OFFSET,
                geometry.cellStart(row) + SGDrawingConstants.SELECTION_OFFSET,
            ),
        size = Size(side, side),
        style = Stroke(width = SGDrawingConstants.SELECTION_STROKE_WIDTH),
    )
}
