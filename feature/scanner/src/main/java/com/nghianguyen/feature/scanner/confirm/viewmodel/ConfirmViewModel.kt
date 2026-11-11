package com.nghianguyen.feature.scanner.confirm.viewmodel

import android.util.Log
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onOk
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.CreateSudokuGameUseCase
import com.nghianguyen.sudoku.PrepareScannedSudokuUseCase
import com.nghianguyen.sudoku.PreparedSudoku
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.ScannedDigitCell
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItems

/**
 * ViewModel for the puzzle confirmation screen. Responsible for validating the scanned Sudoku
 * puzzle and creating a new game session.
 *
 * @property prepareScannedSudokuUseCase Use case for building and solving the scanned Sudoku grid.
 * @property createSudokuGame Use case for creating a game from a prepared Sudoku puzzle.
 */
class ConfirmViewModel(
    private val prepareScannedSudokuUseCase: PrepareScannedSudokuUseCase,
    private val createSudokuGame: CreateSudokuGameUseCase,
) : BaseViewModel<ConfirmScreenState, ConfirmAction, ConfirmEvent>() {
    private var preparedSudoku: PreparedSudoku? = null
    private val logTag = "ConfirmViewModel"

    override fun buildInitialState(): ConfirmScreenState =
        ConfirmScreenState(emptyList(), SudokuGridState(), false)

    /**
     * Initializes the state with the list of scanned digits. Attempts to solve the board to verify
     * its validity.
     *
     * @param scannedDigits The list of digits detected by the scanner.
     */
    fun prepareScannedSudoku(scannedDigits: List<ScannedDigitCell>) {
        preparedSudoku = null
        prepareScannedSudokuUseCase(scannedDigits)
            .onOk { puzzle ->
                preparedSudoku = puzzle
                showPuzzle(puzzle.cells, isValid = true)
            }
            .onErr { failure ->
                Log.e(logTag, "Puzzle preparation failed", failure.cause)
                showPuzzle(failure.cells, isValid = false)
            }
    }

    private fun showPuzzle(digits: List<DigitCell>, isValid: Boolean) {
        val givenItems = digits.filter { it.state is CellState.Given }.toDigitCellItems()
        updateState {
            copy(
                digits = digits,
                sudokuGridState = sudokuGridState.copy(givenItems = givenItems),
                isValid = isValid,
            )
        }
    }

    override fun handleAction(action: ConfirmAction) {
        Log.d(logTag, "handleAction: $action")
        when (action) {
            ConfirmAction.Confirm -> confirmSudoku()
        }
    }

    private fun confirmSudoku() {
        val newSudoku = preparedSudoku ?: return

        launch {
            createSudokuGame(newSudoku)
                .onSuccess {
                    Log.d(logTag, "new game id: $it")
                    sendEvent(ConfirmEvent.ConfirmedGame(it))
                }
                .onFailure { Log.d(logTag, "Error: $it") }
        }
    }
}
