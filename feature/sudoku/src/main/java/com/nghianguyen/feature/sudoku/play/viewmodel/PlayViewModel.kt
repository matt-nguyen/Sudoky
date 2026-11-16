package com.nghianguyen.feature.sudoku.play.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.get
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.ui.component.sudokugrid.DigitCellItem
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Play screen, responsible for managing the state and logic of a Sudoku game.
 *
 * @param gameId The ID of the Sudoku game to play.
 * @param sudokuGameRepository Repository for accessing and updating Sudoku game data.
 */
class PlayViewModel(gameId: Long, private val sudokuGameRepository: SudokuGameRepository) :
    BaseViewModel<PlayAction, PlayEvent>() {
    private val logTag = "PlayViewModel"

    private val _uiState = MutableStateFlow(PlayScreenState())

    private val currentGame =
        sudokuGameRepository
            .getGame(gameId)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    private val observedGame = currentGame.mapNotNull { gameResult ->
        gameResult
            .onFailure { Log.d(logTag, "Error getting game: $it") }
            .get()
    }
    val uiState =
        combine(_uiState, observedGame) { state, game ->
            state.withDigitCells(game.cells).copy(showFinishedDialog = game.isFinished())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _uiState.value)

    override fun handleAction(action: PlayAction) {
        Log.d(logTag, "handleAction: $action")
        when (action) {
            is PlayAction.OnDigitEntered -> {
                enterDigit(action.digit, action.row, action.col)
            }
            PlayAction.OnDeleteGame -> {
                deleteGameAndExit()
            }
        }
    }

    /**
     * Maps the UI state using the provided list of [DigitCell]s. Categorizes cells into
     * given, correct, and incorrect for visual representation.
     */
    private fun PlayScreenState.withDigitCells(digitCells: List<DigitCell>): PlayScreenState {
        val givenItems = mutableListOf<DigitCellItem>()
        val correctItems = mutableListOf<DigitCellItem>()
        val incorrectItems = mutableListOf<DigitCellItem>()

        digitCells.forEach { cell ->
            val item = cell.toDigitCellItem()
            when (cell.state) {
                is CellState.Given -> givenItems.add(item)
                CellState.Empty -> Unit
                is CellState.Entered -> {
                    if (cell.isCorrect()) correctItems.add(item) else incorrectItems.add(item)
                }
            }
        }

        return copy(
            sudokuGridState =
                SudokuGridState(
                    correctItems = correctItems,
                    incorrectItems = incorrectItems,
                    givenItems = givenItems,
                ),
        )
    }

    /** Updates the current game with the [digit] entered at the specified [row] and [col]. */
    private fun enterDigit(digit: Int, row: Int, col: Int) {
        launch {
            currentGame
                .firstOrNull()
                ?.onSuccess { game ->
                    game?.let {
                        it.setDigit(digit, row, col)
                            .onSuccess { sudokuGameRepository.updateGame(it) }
                            .onFailure { Log.d(logTag, "Error updating game: $it") }
                    }
                }
                ?.onFailure{ Log.d(logTag, "Error getting game: $it") }
        }
    }

    /** Deletes the current game and notifies the UI to exit the Play screen. */
    private fun deleteGameAndExit() {
        launch {
            currentGame
                .firstOrNull()
                ?.onSuccess { game ->
                    game?.let {
                        sudokuGameRepository
                            .deleteGame(game)
                            .onSuccess { Log.d(logTag, "Game deleted") }
                            .onFailure { Log.d(logTag, "Error deleting game: $it") }
                    }

                    sendEvent(PlayEvent.GameDeleted)
                }
                ?.onFailure { Log.d(logTag, "Error getting game: $it") }
        }
    }
}
