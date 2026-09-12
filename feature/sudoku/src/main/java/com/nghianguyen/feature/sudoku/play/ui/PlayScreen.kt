package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayAction
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayEvent
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayScreenState
import com.nghianguyen.ui.component.sudokugrid.SudokuGrid
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

/**
 * Screen for playing a Sudoku game, displaying the grid and providing controls for digit entry.
 *
 * @param state The current UI state.
 * @param event Flow of one-time events from the ViewModel.
 * @param onAction Callback for handling user actions.
 * @param onScreenResult Callback for communicating screen results.
 */
@Composable
fun PlayScreen(
    state: PlayScreenState,
    event: SharedFlow<PlayEvent>,
    onAction: (PlayAction) -> Unit,
    onScreenResult: (PlayScreenResult) -> Unit,
) {
    var showFinishedDialog by remember { mutableStateOf(false) }

    val spacing = LocalSpacing.current

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                PlayEvent.GameFinished -> {
                    showFinishedDialog = true
                }
                PlayEvent.GameDeleted -> {
                    showFinishedDialog = false
                    onScreenResult(PlayScreenResult.Exit)
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PlayContent(state = state, onAction = onAction)
            ExitButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(spacing.large),
                onClick = { onScreenResult(PlayScreenResult.Exit) },
            )
        }

        if (showFinishedDialog) {
            FinishedGameDialog(onDismiss = { onAction(PlayAction.OnDeleteGame) })
        }
    }
}

/**
 * Displays the core content of the play screen, including the Sudoku grid and keypad.
 *
 * @param state The current UI state.
 * @param onAction Callback for handling user actions.
 */
@Composable
fun PlayContent(state: PlayScreenState, onAction: (PlayAction) -> Unit) {
    var selectedRow by remember { mutableIntStateOf(0) }
    var selectedCol by remember { mutableIntStateOf(0) }
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        SudokuGrid(state.sudokuGridState) { row, col ->
            selectedRow = row
            selectedCol = col
        }
        DigitKeypad(selectedRow = selectedRow, selectedCol = selectedCol, onAction = onAction)
    }
}
