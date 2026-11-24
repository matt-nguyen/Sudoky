package com.nghianguyen.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.sudoku.model.MAX_DIGIT
import com.nghianguyen.ui.theme.LocalSpacing

private const val KEYPAD_COLUMNS = 5
private const val MODE_KEYPAD_COLUMNS = 4
private val DIGIT_BUTTON_SIZE = 64.dp

/**
 * A numeric keypad for digit entry and clearing.
 *
 * @param onDigitEntered Callback for digit entry and clearing.
 * @param enabled Whether digit entry is enabled.
 * @param extraKey Optional key displayed after Clear. The supplied modifier matches the other keys.
 */
@Composable
fun DigitKeypad(
    onDigitEntered: (Int) -> Unit,
    enabled: Boolean = true,
    extraKey: (@Composable (Modifier) -> Unit)? = null,
) {
    val clearKey = MAX_DIGIT + 1
    val keyCount = clearKey + if (extraKey == null) 0 else 1
    val columns = if (extraKey == null) KEYPAD_COLUMNS else MODE_KEYPAD_COLUMNS
    val spacing = LocalSpacing.current

    Column(
        modifier =
            Modifier.widthIn(
                    max =
                        DIGIT_BUTTON_SIZE * columns +
                            spacing.small * (columns - 1) +
                            spacing.medium * 2,
                )
                .fillMaxWidth()
                .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        for (firstKey in 1..keyCount step columns) {
            val lastKey = minOf(firstKey + columns - 1, keyCount)
            val emptySlots = columns - (lastKey - firstKey + 1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally),
            ) {
                for (key in firstKey..lastKey) {
                    val keyModifier = Modifier.weight(1f).aspectRatio(1f)
                    if (key > clearKey) {
                        extraKey?.invoke(keyModifier)
                    } else {
                        val digit = if (key == clearKey) EMPTY_CELL_VALUE else key
                        DigitButton(
                            text = if (digit == EMPTY_CELL_VALUE) "Clear" else digit.toString(),
                            enabled = enabled,
                            modifier = keyModifier,
                            onClick = { onDigitEntered(digit) },
                        )
                    }
                }
                if (emptySlots > 0) Spacer(Modifier.weight(emptySlots.toFloat()))
            }
        }
    }
}
