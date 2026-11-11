package com.nghianguyen.ui.component.sudokugrid

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/** Theme-derived colors and text styles used to draw the Sudoku grid. */
internal data class SudokuGridStyle(
    val gridColor: Color,
    val selectionColor: Color,
    val backgroundColor: Color,
    val wrongBackgroundColor: Color,
    val givenDigitTextStyle: TextStyle,
    val enteredDigitTextStyle: TextStyle,
    val wrongDigitTextStyle: TextStyle,
)

/** Remembers the grid style, recreating it when theme colors or typography change. */
@Composable
internal fun rememberSudokuGridStyle(): SudokuGridStyle {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    return remember(colorScheme, typography) {
        val givenDigitTextStyle =
            typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = SGDrawingConstants.FONT_SIZE,
                color = colorScheme.onSurface,
            )
        val enteredDigitTextStyle =
            typography.bodyLarge.copy(
                fontSize = SGDrawingConstants.FONT_SIZE,
                color = colorScheme.primary,
            )
        val wrongDigitTextStyle =
            typography.bodyLarge.copy(
                color = colorScheme.onErrorContainer,
                fontSize = SGDrawingConstants.FONT_SIZE,
            )
        val wrongBackgroundColor = colorScheme.errorContainer

        SudokuGridStyle(
            gridColor = colorScheme.onSurface,
            selectionColor = colorScheme.primary,
            backgroundColor = colorScheme.surface,
            wrongBackgroundColor = wrongBackgroundColor,
            givenDigitTextStyle = givenDigitTextStyle,
            enteredDigitTextStyle = enteredDigitTextStyle,
            wrongDigitTextStyle = wrongDigitTextStyle,
        )
    }
}
