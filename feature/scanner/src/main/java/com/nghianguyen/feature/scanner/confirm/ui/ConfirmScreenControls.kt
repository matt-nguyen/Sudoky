package com.nghianguyen.feature.scanner.confirm.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nghianguyen.ui.theme.LocalSpacing

/**
 * Displays the redo, confirm, and exit controls for a scanned Sudoku puzzle.
 *
 * @param isValid Whether the scanned Sudoku grid can be confirmed.
 * @param onConfirm Callback invoked when the user confirms the grid.
 * @param onRedo Callback invoked when the user rescans the grid.
 * @param onExit Callback invoked when the user exits the scanner flow.
 */
@Composable
fun ConfirmScreenControls(
    isValid: Boolean,
    onConfirm: () -> Unit,
    onRedo: () -> Unit,
    onExit: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        OutlinedButton(
            onClick = onRedo,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text("Redo")
        }

        OutlinedButton(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            enabled = isValid,
            border =
                BorderStroke(
                    1.dp,
                    if (isValid) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    containerColor =
                        if (isValid) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
                    contentColor =
                        if (isValid) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                ),
        ) {
            Text("Confirm")
        }
    }

    OutlinedButton(
        onClick = onExit,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text("Exit")
    }
}
