package com.nghianguyen.ui.component.sudokugrid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer

/**
 * A Composable that renders a 9x9 Sudoku grid. It displays given digits, correctly entered digits,
 * and incorrectly entered digits with different visual styles. It also supports cell selection.
 *
 * @param state The current state of the grid, including the digits to display.
 * @param modifier The modifier applied to the grid.
 * @param controlledSelection Whether the caller owns selection.
 * @param selection Zero-based coordinates when selection is controlled.
 * @param onCellSelected Callback triggered when a cell is tapped, providing the (row, col) indices.
 */
@Composable
fun SudokuGrid(
    state: SudokuGridState,
    modifier: Modifier = Modifier,
    controlledSelection: Boolean = false,
    selection: Pair<Int, Int>? = null,
    onCellSelected: ((Int, Int) -> Unit)? = null,
) {
    // Cache digits 1-9 for both the bold given style and the regular entered styles.
    val textMeasurer = rememberTextMeasurer(cacheSize = SGDrawingConstants.GRID_SIZE * 2)
    val style = rememberSudokuGridStyle()
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val currentOnCellSelected by rememberUpdatedState(onCellSelected)
    val usesControlledSelection by rememberUpdatedState(controlledSelection)
    val selectionEnabled = onCellSelected != null

    Canvas(
        modifier =
            modifier.fillMaxWidth().aspectRatio(1f).background(style.backgroundColor).pointerInput(
                selectionEnabled,
            ) {
                if (selectionEnabled) {
                    detectTapGestures { tap ->
                        val geometry = SudokuGridGeometry(minOf(size.width, size.height).toFloat())
                        val row = geometry.mapCoord(tap.y)
                        val col = geometry.mapCoord(tap.x)
                        if (row != null && col != null) {
                            if (!usesControlledSelection) selectedCell = row to col
                            currentOnCellSelected?.invoke(row, col)
                        }
                    }
                }
            },
    ) {
        drawSudokuGrid(
            state = state,
            geometry = SudokuGridGeometry(minOf(size.width, size.height)),
            style = style,
            selectedCell =
                if (controlledSelection) selection else selectedCell.takeIf { selectionEnabled },
            textMeasurer = textMeasurer,
        )
    }
}
