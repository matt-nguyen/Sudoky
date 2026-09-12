package com.nghianguyen.feature.scanner.confirm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.nghianguyen.feature.scanner.confirm.ui.ConfirmScreenResult.ConfirmedGame
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmAction
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmEvent
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmScreenState
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
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.large),
        ) {
            Text(
                text = "Confirm Puzzle",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            SudokuGrid(state.sudokuGridState)

            if (!state.isValid) {
                Text(
                    text = "Invalid Sudoku grid. Please rescan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ConfirmScreenControls(
                isValid = state.isValid,
                onConfirm = { onAction(ConfirmAction.Confirm) },
                onRedo = { onScreenResult(ConfirmScreenResult.Redo) },
                onExit = { onScreenResult(ConfirmScreenResult.Exit) },
            )
        }
    }
}
