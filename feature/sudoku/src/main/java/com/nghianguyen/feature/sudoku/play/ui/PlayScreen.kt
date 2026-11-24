package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.nghianguyen.feature.sudoku.R
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayAction
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayEvent
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayScreenState
import com.nghianguyen.ui.component.DigitButton
import com.nghianguyen.ui.component.DigitKeypad
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
    val spacing = LocalSpacing.current

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                PlayEvent.GameDeleted -> {
                    onScreenResult(PlayScreenResult.Exit)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(spacing.large)) {
                ExitButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onScreenResult(PlayScreenResult.Exit) },
                )
            }
        },
    ) { paddingValues ->
        PlayContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues),
        )

        if (state.showFinishedDialog) {
            FinishedGameDialog(onDismiss = { onAction(PlayAction.OnDeleteGame) })
        }
    }
}

/**
 * Displays the core content of the play screen, including the Sudoku grid and keypad.
 *
 * @param state The current UI state.
 * @param onAction Callback for handling user actions.
 * @param modifier Modifier applied to the content layout.
 */
@Composable
fun PlayContent(
    state: PlayScreenState,
    onAction: (PlayAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val selection = state.selectedCell
    val selectedCellDescription = selection?.let { (row, col) ->
        val notes =
            state.sudokuGridState.noteItems
                .filter { it.row == row && it.col == col }
                .map { it.digit }
                .sorted()
        if (notes.isEmpty()) {
            stringResource(R.string.play_selected_cell, row + 1, col + 1)
        } else {
            stringResource(
                R.string.play_selected_cell_notes,
                row + 1,
                col + 1,
                notes.joinToString(),
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        SudokuGrid(
            state = state.sudokuGridState,
            modifier =
                Modifier.semantics { selectedCellDescription?.let { stateDescription = it } },
            controlledSelection = true,
            selection = selection,
            onCellSelected = { row, col -> onAction(PlayAction.OnCellSelected(row, col)) },
        )
        DigitKeypad(
            onDigitEntered = { digit -> onAction(PlayAction.OnDigitEntered(digit)) },
            enabled = selection != null && !state.showFinishedDialog,
            extraKey = { keyModifier ->
                NotesModeKey(
                    notesMode = state.notesMode,
                    onToggle = { onAction(PlayAction.OnNotesModeToggled) },
                    modifier = keyModifier,
                )
            },
        )
    }
}

@Composable
private fun NotesModeKey(notesMode: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.play_notes)
    val mode =
        stringResource(
            if (notesMode) R.string.play_notes_mode else R.string.play_digits_mode,
        )

    DigitButton(
        onClick = onToggle,
        modifier =
            modifier.semantics {
                role = Role.Switch
                contentDescription = label
                stateDescription = mode
                toggleableState = ToggleableState(notesMode)
            },
    ) {
        Icon(
            imageVector = if (notesMode) Icons.Outlined.EditNote else Icons.Outlined.Edit,
            contentDescription = null,
        )
    }
}
