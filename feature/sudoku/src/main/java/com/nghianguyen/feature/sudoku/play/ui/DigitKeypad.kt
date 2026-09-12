package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayAction
import com.nghianguyen.sudoku.model.BOX_SIZE
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.ui.theme.LocalSpacing

/**
 * A numeric keypad for digit entry and clearing.
 *
 * @param selectedRow The currently selected row.
 * @param selectedCol The currently selected column.
 * @param onAction Callback for digit entry actions.
 */
@Composable
fun DigitKeypad(selectedRow: Int, selectedCol: Int, onAction: (PlayAction) -> Unit) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth(PlayScreenConstants.KEYPAD_WIDTH_FRACTION)
                .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        for (rowIndex in 0 until BOX_SIZE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally),
            ) {
                for (colIndex in 1..BOX_SIZE) {
                    val digit = rowIndex * BOX_SIZE + colIndex
                    DigitButton(
                        text = digit.toString(),
                        modifier = Modifier.size(PlayScreenConstants.DIGIT_BUTTON_SIZE),
                        onClick = {
                            onAction(PlayAction.OnDigitEntered(digit, selectedRow, selectedCol))
                        },
                    )
                }
            }
        }

        DigitButton(
            text = "Clear",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(PlayAction.OnDigitEntered(EMPTY_CELL_VALUE, selectedRow, selectedCol))
            },
        )
    }
}
