package com.nghianguyen.feature.sudoku.play.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.get
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.EditSudokuNotesUseCase
import com.nghianguyen.sudoku.EnterSudokuDigitUseCase
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.ui.component.sudokugrid.DigitCellItem
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Play screen, responsible for managing the state and logic of a Sudoku game.
 *
 * @param gameId The ID of the Sudoku game to play.
 * @param sudokuGameRepository Repository for observing and deleting Sudoku games.
 * @param enterSudokuDigit Use case for atomically entering an answer.
 * @param editSudokuNotes Use case for atomically editing candidate notes.
 */
class PlayViewModel(
    private val gameId: Long,
    private val sudokuGameRepository: SudokuGameRepository,
    private val enterSudokuDigit: EnterSudokuDigitUseCase,
    private val editSudokuNotes: EditSudokuNotesUseCase,
) : BaseViewModel<PlayAction, PlayEvent>() {
    private val logTag = "PlayViewModel"

    private val _uiState = MutableStateFlow(PlayScreenState())

    private val currentGame =
        sudokuGameRepository
            .getGame(gameId)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    private val observedGame = currentGame.mapNotNull { gameResult ->
        gameResult.onFailure { Log.d(logTag, "Error getting game: $it") }.get()
    }
    val uiState =
        combine(_uiState, observedGame) { state, game ->
                state.withDigitCells(game.cells).copy(showFinishedDialog = game.isFinished())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _uiState.value)

    // Queue synchronously at the action boundary so rapid taps retain their order.
    private data class KeypadEdit(
        val digit: Int,
        val row: Int,
        val col: Int,
        val notesMode: Boolean,
    )

    private val digitEntries = Channel<KeypadEdit>(Channel.UNLIMITED)

    init {
        launch {
            for (entry in digitEntries) {
                val result =
                    if (entry.notesMode && entry.digit != EMPTY_CELL_VALUE) {
                        editSudokuNotes(gameId, entry.digit, entry.row, entry.col)
                    } else {
                        enterSudokuDigit(gameId, entry.digit, entry.row, entry.col)
                    }
                result.onFailure { Log.d(logTag, "Error applying keypad edit: $it") }
            }
        }
    }

    override fun onCleared() {
        digitEntries.cancel()
        super.onCleared()
    }

    override fun handleAction(action: PlayAction) {
        Log.d(logTag, "handleAction: $action")
        when (action) {
            is PlayAction.OnDigitEntered -> {
                enqueueKeypadEdit(action.digit)
            }
            is PlayAction.OnCellSelected -> selectCell(action.row, action.col)
            PlayAction.OnNotesModeToggled -> toggleNotesMode()
            PlayAction.OnDeleteGame -> {
                deleteGameAndExit()
            }
        }
    }

    private fun selectCell(row: Int, col: Int) {
        _uiState.update { it.copy(selectedCell = row to col) }
    }

    private fun toggleNotesMode() {
        _uiState.update { it.copy(notesMode = !it.notesMode) }
    }

    private fun enqueueKeypadEdit(digit: Int) {
        val state = _uiState.value
        val (row, col) = state.selectedCell ?: return
        digitEntries.trySend(KeypadEdit(digit, row, col, state.notesMode))
    }

    /**
     * Maps the UI state using the provided list of [DigitCell]s. Categorizes cells into given,
     * correct, and incorrect for visual representation.
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
                    noteItems =
                        digitCells.flatMap { cell ->
                            cell.notes.map {
                                DigitCellItem(it.value, cell.row.value, cell.col.value)
                            }
                        },
                ),
        )
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
