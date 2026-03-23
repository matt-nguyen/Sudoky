package com.nghianguyen.ui.component.sudokugrid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

/**
 * A Composable that renders a 9x9 Sudoku grid. It displays given digits, correctly entered digits,
 * and incorrectly entered digits with different visual styles. It also supports cell selection.
 *
 * @param state The current state of the grid, including the digits to display.
 * @param onCellSelected Callback triggered when a cell is tapped, providing the (row, col) indices.
 */
@Composable
fun SudokuGrid(state: SudokuGridState, onCellSelected: ((Int, Int) -> Unit)? = null) {
    val textMeasurer = rememberTextMeasurer()
    var tapPoint by remember { mutableStateOf<Offset?>(null) }
    var cellSize by remember { mutableFloatStateOf(0f) }

    val thickWidth = 8f
    val thinWidth = 2f

    val gridColor = MaterialTheme.colorScheme.onSurface
    val selectionColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface

    fun getLineWidth(i: Int): Float = if (i % 3 == 0) thickWidth else thinWidth

    // Calculates the px offset at 'lineIndex'
    // 'lineIndex' is the index of the grid line. Indices include the outer borders.
    fun getLineOffset(lineIndex: Int, cellSize: Float): Float {
        var offset = 0f
        for (j in 0 until lineIndex) {
            offset += getLineWidth(j) + cellSize
        }
        offset += getLineWidth(lineIndex) / 2f
        return offset
    }

    // Calculates the center px coordinate for a given cell index.
    // Use for both row (y) and column (x).
    fun getCellCenter(cellIndex: Int, cellSize: Float): Float {
        val start = getLineOffset(cellIndex, cellSize) + getLineWidth(cellIndex) / 2f
        return start + cellSize / 2f
    }

    // Maps a px coordinate to a cell index (0-8).
    // Use y-coordinate to get row and x-coordinate to get column.
    fun mapCoord(coord: Float): Int {
        for (i in 0..8) {
            val start = getLineOffset(i, cellSize) + getLineWidth(i) / 2f
            if (coord >= start && coord <= start + cellSize) return i
        }
        return if (coord < getLineOffset(0, cellSize)) 0 else 8
    }
    // TODO test this still works
    LaunchedEffect(tapPoint) {
        tapPoint?.let { tap ->
            if (cellSize > 0) {
                onCellSelected?.invoke(mapCoord(tap.y), mapCoord(tap.x))
            }
        }
    }

    // TODO move 'selectedRowCol' into SudokuGridState
    val selectedRowCol: Pair<Int, Int>? =
        remember(tapPoint, cellSize) {
            tapPoint?.let { tap ->
                if (cellSize > 0) {
                    Pair(mapCoord(tap.y), mapCoord(tap.x))
                } else {
                    null
                }
            }
        }

    val givenDigitTextStyle =
        MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    val enteredDigitTextStyle =
        MaterialTheme.typography.bodyLarge.copy(
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    val wrongDigitTextStyle =
        MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 24.sp,
        )
    val wrongBackgroundColor = MaterialTheme.colorScheme.errorContainer

    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier =
                Modifier.fillMaxWidth()
                    .aspectRatio(1f)
                    .background(backgroundColor)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectTapGestures { detectedTap -> tapPoint = detectedTap }
                    }
        ) {
            val sideSize = size.width
            val totalLineWidth = 4 * thickWidth + 6 * thinWidth
            cellSize = (sideSize - totalLineWidth) / 9f

            // Draw backgrounds for incorrect items first so lines are on top
            state.incorrectItems.forEach { (_, row, col) ->
                val cellCenterX = getCellCenter(col, cellSize)
                val cellCenterY = getCellCenter(row, cellSize)
                drawRect(
                    color = wrongBackgroundColor,
                    topLeft = Offset(cellCenterX - cellSize / 2, cellCenterY - cellSize / 2),
                    size = Size(cellSize, cellSize),
                )
            }

            // Draw Grid Lines (10 lines in each direction)
            for (i in 0..9) {
                val offset = getLineOffset(i, cellSize)
                val width = getLineWidth(i)
                // Horizontal
                drawLine(gridColor, Offset(0f, offset), Offset(sideSize, offset), width)
                // Vertical
                drawLine(gridColor, Offset(offset, 0f), Offset(offset, sideSize), width)
            }

            fun drawDigit(digit: Int, row: Int, col: Int, style: TextStyle) {
                val cellCenterX = getCellCenter(col, cellSize)
                val cellCenterY = getCellCenter(row, cellSize)
                val result = textMeasurer.measure(digit.toString(), style)
                drawText(
                    textLayoutResult = result,
                    topLeft =
                        Offset(
                            cellCenterX - result.size.width / 2,
                            cellCenterY - result.size.height / 2,
                        ),
                )
            }

            state.givenItems.forEach { (digit, row, col) ->
                drawDigit(digit, row, col, givenDigitTextStyle)
            }
            state.correctItems.forEach { (digit, row, col) ->
                drawDigit(digit, row, col, enteredDigitTextStyle)
            }
            state.incorrectItems.forEach { (digit, row, col) ->
                drawDigit(digit, row, col, wrongDigitTextStyle)
            }

            if (onCellSelected != null) {
                selectedRowCol?.let { (row, col) ->
                    val cellCenterX = getCellCenter(col, cellSize)
                    val cellCenterY = getCellCenter(row, cellSize)
                    val selectionStrokeWidth = 8f
                    drawRect(
                        color = selectionColor,
                        topLeft =
                            Offset(
                                cellCenterX - cellSize / 2 + 4f,
                                cellCenterY - cellSize / 2 + 4f,
                            ),
                        size = Size(cellSize - 8f, cellSize - 8f),
                        style = Stroke(width = selectionStrokeWidth),
                    )
                }
            }
        }
    }
}
