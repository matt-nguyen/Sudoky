package com.nghianguyen.feature.scanner.confirm.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmAction
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmScreenState
import com.nghianguyen.ui.component.DigitButton
import com.nghianguyen.ui.component.DigitKeypad
import com.nghianguyen.ui.theme.LocalSpacing

/**
 * Displays the redo, confirm, and exit controls for a scanned Sudoku puzzle.
 *
 * @param enabled Whether the scanned Sudoku grid can be confirmed.
 * @param onModify Callback invoked when the user edits the grid.
 * @param onConfirm Callback invoked when the user confirms the grid.
 * @param onRedo Callback invoked when the user rescans the grid.
 * @param onExit Callback invoked when the user exits the scanner flow.
 */
@Composable
fun ConfirmScreenControls(
    enabled: Boolean,
    onConfirm: () -> Unit,
    onModify: (() -> Unit)?,
    onRedo: () -> Unit,
    onExit: () -> Unit,
) {
    val spacing = LocalSpacing.current
    if (onModify != null) {
        OutlinedButton(onClick = onModify, enabled = enabled) { Text("Modify") }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        OutlinedButton(
            onClick = onRedo,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text("Redo")
        }

        OutlinedButton(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            border =
                BorderStroke(
                    1.dp,
                    if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    containerColor =
                        if (enabled) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
                    contentColor =
                        if (enabled) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                ),
        ) {
            Text("Confirm")
        }
    }

    OutlinedButton(
        onClick = onExit,
        enabled = enabled,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text("Exit")
    }
}

/** Displays the keypad and navigation controls while correcting a scanned puzzle. */
@Composable
fun ConfirmEditControls(state: ConfirmScreenState, onAction: (ConfirmAction) -> Unit) {
    val spacing = LocalSpacing.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        DigitKeypad(
            onDigitEntered = { onAction(ConfirmAction.EnterDigit(it)) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            DigitButton(
                text = "Back",
                onClick = { onAction(ConfirmAction.Back) },
                modifier = Modifier.weight(1f),
                enabled = !state.isSubmitting,
            )
            DigitButton(
                text = "Confirm",
                onClick = { onAction(ConfirmAction.Confirm) },
                modifier = Modifier.weight(1f),
                enabled = !state.isSubmitting,
            )
        }
    }
}
