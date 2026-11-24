package com.nghianguyen.feature.scanner.confirm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.nghianguyen.feature.scanner.confirm.ui.ConfirmScreenResult.ConfirmedGame
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmAction
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmEvent
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmScreenState
import com.nghianguyen.feature.scanner.confirm.viewmodel.ValidationStatus
import com.nghianguyen.ui.component.sudokugrid.SudokuGrid
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

/**
 * Screen for reviewing and confirming interpreted Sudoku scan results.
 *
 * @param state Current UI state.
 * @param event Flow of one-time events.
 * @param onAction User interaction callback.
 * @param onScreenResult Navigation and result callback.
 */
@Composable
fun ConfirmScreen(
    state: ConfirmScreenState,
    event: SharedFlow<ConfirmEvent>,
    onAction: (ConfirmAction) -> Unit,
    onScreenResult: (ConfirmScreenResult) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is ConfirmEvent.ConfirmedGame -> {
                    onScreenResult(ConfirmedGame(it.gameId))
                }
            }
        }
    }

    val spacing = LocalSpacing.current

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Text(
                text = "Confirm Puzzle",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                key(state.isEditing) {
                    SudokuGrid(
                        state = state.sudokuGridState,
                        modifier = Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true),
                        onCellSelected =
                            if (state.isEditing && !state.isSubmitting) {
                                { row, col -> onAction(ConfirmAction.SelectCell(row, col)) }
                            } else null,
                    )
                }
            }

            ConfirmWarning(state)

            if (state.isEditing) {
                ConfirmEditControls(state = state, onAction = onAction)
            } else {
                ConfirmScreenControls(
                    enabled = !state.isSubmitting,
                    onModify = { onAction(ConfirmAction.Modify) },
                    onConfirm = { onAction(ConfirmAction.Confirm) },
                    onRedo = { onScreenResult(ConfirmScreenResult.Redo) },
                    onExit = { onScreenResult(ConfirmScreenResult.Exit) },
                )
            }
        }
    }
}

@Composable
private fun ConfirmWarning(state: ConfirmScreenState) {
    val warning =
        state.warning
            ?: if (
                state.validationStatus == ValidationStatus.Invalid &&
                    !state.isEditing &&
                    !state.isSubmitting
            ) {
                "Invalid Sudoku grid. Please modify or rescan."
            } else null
    if (warning != null) {
        Text(
            text = warning,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}
