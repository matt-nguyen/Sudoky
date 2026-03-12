package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayAction
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayEvent
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayScreenState
import com.nghianguyen.ui.component.sudokugrid.SudokuGrid
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    state: PlayScreenState,
    event: SharedFlow<PlayEvent>,
    onAction: (PlayAction) -> Unit,
    onScreenResult: (PlayScreenResult) -> Unit
) {
    var showFinishedDialog by remember { mutableStateOf(false) }

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

    val spacing = LocalSpacing.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                var selectedRow by remember { mutableIntStateOf(0) }
                var selectedCol by remember { mutableIntStateOf(0) }

                SudokuGrid(state.sudokuGridState) { row, col ->
                    selectedRow = row
                    selectedCol = col
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    (0..2).forEach { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally)
                        ) {
                            (1..3).forEach { colIndex ->
                                val digit = rowIndex * 3 + colIndex
                                DigitButton(
                                    text = digit.toString(),
                                    modifier = Modifier.size(64.dp),
                                    onClick = {
                                        onAction(PlayAction.OnDigitEntered(digit, selectedRow, selectedCol))
                                    }
                                )
                            }
                        }
                    }

                    DigitButton(
                        text = "Clear",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onAction(PlayAction.OnDigitEntered(0, selectedRow, selectedCol))
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = { onScreenResult(PlayScreenResult.Exit) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(spacing.large)
                    .size(56.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        if (showFinishedDialog) {
            BasicAlertDialog(
                onDismissRequest = {
                    onAction(PlayAction.OnDeleteGame)
                }
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Game Finished!")
                    }
                }
            }
        }
    }
}
