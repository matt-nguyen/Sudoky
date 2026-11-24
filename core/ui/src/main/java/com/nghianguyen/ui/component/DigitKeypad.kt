package com.nghianguyen.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.ui.theme.LocalSpacing

private const val KEYPAD_ROWS = 2
private const val KEYPAD_COLUMNS = 5
private val DIGIT_BUTTON_SIZE = 64.dp

/**
 * A numeric keypad for digit entry and clearing.
 *
 * @param onDigitEntered Callback for digit entry and clearing.
 * @param enabled Whether digit entry is enabled.
 */
@Composable
fun DigitKeypad(onDigitEntered: (Int) -> Unit, enabled: Boolean = true) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            Modifier.widthIn(
                    max =
                        DIGIT_BUTTON_SIZE * KEYPAD_COLUMNS +
                            spacing.small * (KEYPAD_COLUMNS - 1) +
                            spacing.medium * 2,
                )
                .fillMaxWidth()
                .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        for (rowIndex in 0 until KEYPAD_ROWS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally),
            ) {
                for (colIndex in 1..KEYPAD_COLUMNS) {
                    val value = rowIndex * KEYPAD_COLUMNS + colIndex
                    val digit =
                        if (value == KEYPAD_ROWS * KEYPAD_COLUMNS) EMPTY_CELL_VALUE else value
                    DigitButton(
                        text = if (digit == EMPTY_CELL_VALUE) "Clear" else digit.toString(),
                        enabled = enabled,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        onClick = { onDigitEntered(digit) },
                    )
                }
            }
        }
    }
}
