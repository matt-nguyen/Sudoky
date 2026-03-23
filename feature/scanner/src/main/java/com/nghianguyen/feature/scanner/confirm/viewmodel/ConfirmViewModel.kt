package com.nghianguyen.feature.scanner.confirm.viewmodel

import android.util.Log
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.SudokuSolver
import com.nghianguyen.sudoku.model.ScannedDigitCell
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import com.nghianguyen.ui.component.sudokugrid.toDigitCellItems

/**
 * ViewModel for the puzzle confirmation screen. Responsible for validating the scanned Sudoku
 * puzzle and creating a new game session.
 *
 * @property sudokuSolver Utility for building and solving the Sudoku grid.
 * @property sudokuGameRepository Repository for managing Sudoku game data.
 */
class ConfirmViewModel(
    private val sudokuSolver: SudokuSolver,
    private val sudokuGameRepository: SudokuGameRepository,
) : BaseViewModel<ConfirmScreenState, ConfirmAction, ConfirmEvent>() {

    override fun buildInitialState(): ConfirmScreenState =
        ConfirmScreenState(emptyList(), SudokuGridState(), false)

    override suspend fun onStart() {
        // No-op
    }

    /**
     * Initializes the state with the list of scanned digits. Attempts to solve the board to verify
     * its validity.
     *
     * @param scannedDigits The list of digits detected by the scanner.
     */
    fun setScannedDigits(scannedDigits: List<ScannedDigitCell>) {
        val buildSudokuGrid = sudokuSolver.buildSudokuGrid(scannedDigits)

        sudokuSolver
            .solveBoard(buildSudokuGrid)
            .onSuccess { digits ->
                val givenItems = digits.filter { it.isGiven }.toDigitCellItems()
                updateState {
                    copy(
                        digits = digits,
                        sudokuGridState = sudokuGridState.copy(givenItems = givenItems),
                        isValid = true,
                    )
                }
            }
            .onFailure {
                Log.e("ConfirmViewModel", "solveBoard failed", it)
                val givenItems = buildSudokuGrid.filter { it.isGiven }.toDigitCellItems()
                updateState {
                    copy(
                        digits = buildSudokuGrid,
                        sudokuGridState = sudokuGridState.copy(givenItems = givenItems),
                        isValid = false,
                    )
                }
            }
    }

    override fun handleAction(action: ConfirmAction) {
        Log.d("ConfirmViewModel", "handleAction: $action")
        when (action) {
            ConfirmAction.Confirm -> confirmSudoku()
        }
    }

    private fun confirmSudoku() {
        if (!uiState.value.isValid) return

        launch {
            sudokuGameRepository
                .newGame(uiState.value.digits)
                .onSuccess {
                    Log.d("ConfirmViewModel", "new game id: $it")
                    sendEvent(ConfirmEvent.ConfirmedGame(it))
                }
                .onFailure { Log.d("ConfirmViewModel", "Error: $it") }
        }
    }
}
