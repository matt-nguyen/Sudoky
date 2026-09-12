package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.ui.unit.dp

/**
 * Constants used for the layout and styling of the Sudoku play screen.
 */
internal object PlayScreenConstants {
    /** The width fraction for the digit keypad. */
    const val KEYPAD_WIDTH_FRACTION = 0.5f

    /** The size of each digit button. */
    val DIGIT_BUTTON_SIZE = 64.dp

    /** The size of the exit button. */
    val EXIT_BUTTON_SIZE = 56.dp

    /** The size of the exit icon. */
    val EXIT_ICON_SIZE = 32.dp

    /** The corner radius for the game-finished dialog. */
    val FINISHED_DIALOG_CORNER_RADIUS = 16.dp

    /** The default width for component borders. */
    val DEFAULT_BORDER_WIDTH = 1.dp
}
