package com.nghianguyen.feature.sudoku.play.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.ui.component.sudokugrid.DigitCellItem
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn

/**
 * ViewModel for the Play screen, responsible for managing the state and logic of a Sudoku game.
 *
 * @param gameId The ID of the Sudoku game to play.
 * @param sudokuGameRepository Repository for accessing and updating Sudoku game data.
 */
class PlayViewModel(
    gameId: Long,
    private val sudokuGameRepository: SudokuGameRepository
): BaseViewModel<PlayScreenState, PlayAction, PlayEvent>() {

    override fun buildInitialState() = PlayScreenState(emptyList())

    private val currentGame = sudokuGameRepository.getGame(gameId)
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            1
        )

    override fun onStart() {
        launch {
            currentGame.collect { gameResult ->
                gameResult
                    .onSuccess { game ->
                        val digitCells = game?.cells ?: return@onSuccess

                        onDigitCells(digitCells)

                        if (game.isFinished()) {
                            sendEvent(PlayEvent.GameFinished)
                        }
                    }
                    .onFailure {
                        Log.d("PlayViewModel", "Error getting game: $it")
                    }
            }
        }
    }

    override fun handleAction(action: PlayAction) {
        Log.d("PlayViewModel", "handleAction: $action")
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
     * Updates the UI state based on the provided list of [DigitCell]s.
     * Categorizes cells into given, correct, and incorrect for visual representation.
     */
    private fun onDigitCells(digitCells: List<DigitCell>){
        val givenItems = mutableListOf<DigitCellItem>()
        val correctItems = mutableListOf<DigitCellItem>()
        val incorrectItems = mutableListOf<DigitCellItem>()

        digitCells.forEach { cell ->
            val item = cell.toDigitCellItem()
            when {
                cell.isGiven -> givenItems.add(item)
                cell.isCorrect() -> correctItems.add(item)
                cell.hasDigitEntered() -> incorrectItems.add(item)
            }
        }

        updateState {
            copy(
                digits = digitCells,
                sudokuGridState = SudokuGridState(
                    correctItems = correctItems,
                    incorrectItems = incorrectItems,
                    givenItems = givenItems
                )
            )
        }
    }

    /**
     * Updates the current game with the [digit] entered at the specified [row] and [col].
     */
    private fun enterDigit(digit: Int, row: Int, col: Int) {
        launch {
            currentGame.firstOrNull()
                ?.onSuccess { game ->
                    game?.let {
                        it.setDigit(digit, row, col)
                            .onSuccess { sudokuGameRepository.updateGame(it) }
                            .onFailure { Log.d("PlayViewModel", "Error updating game: $it") }
                    }
                }
                ?.onFailure {
                    Log.d("PlayViewModel", "Error getting game: $it")
                }
        }
    }

    /**
     * Deletes the current game and notifies the UI to exit the Play screen.
     */
    private fun deleteGameAndExit() {
        launch {
            currentGame.firstOrNull()
                ?.onSuccess { game ->
                    game?.let {
                        sudokuGameRepository.deleteGame(game)
                            .onSuccess { Log.d("PlayViewModel", "Game deleted") }
                            .onFailure { Log.d("PlayViewModel", "Error deleting game: $it") }
                    }

                    sendEvent(PlayEvent.GameDeleted)
                }
                ?.onFailure {
                    Log.d("PlayViewModel", "Error getting game: $it")
                }
        }
    }
}
