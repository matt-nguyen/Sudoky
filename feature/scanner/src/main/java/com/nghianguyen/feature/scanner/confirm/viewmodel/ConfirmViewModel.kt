package com.nghianguyen.feature.scanner.confirm.viewmodel

import android.util.Log
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.CreateSudokuGameUseCase
import com.nghianguyen.sudoku.PrepareScannedSudokuUseCase
import com.nghianguyen.sudoku.PreparedSudoku
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Digit
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.sudoku.model.MAX_DIGIT
import com.nghianguyen.sudoku.model.ScannedDigitCell
import com.nghianguyen.sudoku.model.digit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

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
) : BaseViewModel<ConfirmAction, ConfirmEvent>() {
    private val logTag = "ConfirmViewModel"
    // Restored when Back is pressed to discard edits.
    private var stateBeforeEditing: ConfirmScreenState? = null

    private val _uiState =
        MutableStateFlow(ConfirmScreenState(digits = emptyList()))
    val uiState = _uiState.asStateFlow()

    /**
     * Initializes the state with the list of scanned digits. Attempts to solve the board to verify
     * its validity.
     *
     * @param scannedDigits The list of digits detected by the scanner.
     */
    fun prepareScannedSudoku(scannedDigits: List<ScannedDigitCell>) {
        stateBeforeEditing = null
        _uiState.value =
            ConfirmScreenState(digits = emptyList(), isSubmitting = true)
        launch {
            withContext(Dispatchers.Default) { prepareScannedSudokuUseCase(scannedDigits) }
                .onOk { puzzle -> showPuzzle(puzzle.cells, ValidationStatus.Valid) }
                .onErr { failure ->
                    Log.e(logTag, "Puzzle preparation failed", failure.cause)
                    showPuzzle(failure.cells, ValidationStatus.Invalid)
                }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }

    private fun showPuzzle(digits: List<DigitCell>, validationStatus: ValidationStatus) {
        _uiState.update { state ->
            state.copy(
                digits = digits,
                validationStatus = validationStatus,
            )
        }
    }

    override fun handleAction(action: ConfirmAction) {
        Log.d(logTag, "handleAction: $action")
        if (_uiState.value.isSubmitting) return
        when (action) {
            ConfirmAction.Modify -> startEditing()
            ConfirmAction.Back -> cancelEditing()
            is ConfirmAction.SelectCell -> selectCell(action.row, action.col)
            is ConfirmAction.EnterDigit -> enterDigit(action.digit)
            ConfirmAction.Confirm -> confirmSudoku()
        }
    }

    private fun startEditing() {
        if (_uiState.value.isEditing) return
        stateBeforeEditing = _uiState.value
        _uiState.update { it.copy(isEditing = true) }
    }

    private fun cancelEditing() {
        stateBeforeEditing?.let { _uiState.value = it }
        stateBeforeEditing = null
    }

    private fun selectCell(row: Int, col: Int) {
        if (!_uiState.value.isEditing) return
        _uiState.update { it.copy(selectedCell = row to col) }
    }

    private fun enterDigit(digit: Int) {
        val state = _uiState.value
        if (!state.isEditing || digit !in EMPTY_CELL_VALUE..MAX_DIGIT) return
        val (row, col) = state.selectedCell ?: return
        val digits =
            state.digits.map { cell ->
                if (cell.row.value == row && cell.col.value == col) {
                    cell.copy(
                        state =
                            if (digit == EMPTY_CELL_VALUE) CellState.Empty
                            else CellState.Given(Digit(digit)),
                        solution = 0,
                    )
                } else cell
            }
        showPuzzle(digits, ValidationStatus.Unchecked)
        _uiState.update { it.copy(warning = null) }
    }

    private fun confirmSudoku() {
        if (!_uiState.value.isEditing) stateBeforeEditing = _uiState.value
        val scannedDigits =
            _uiState.value.digits.mapNotNull { cell ->
                cell.state.digit?.let {
                    ScannedDigitCell(it.value.toString(), cell.row.value, cell.col.value)
                }
            }
        _uiState.update { it.copy(isSubmitting = true, warning = null) }
        launch {
            withContext(Dispatchers.Default) { prepareScannedSudokuUseCase(scannedDigits) }
                .onOk { puzzle -> savePreparedPuzzle(puzzle) }
                .onErr { showValidationFailure() }
        }
    }

    private suspend fun savePreparedPuzzle(puzzle: PreparedSudoku) {
        createSudokuGame(puzzle)
            .onOk { sendEvent(ConfirmEvent.ConfirmedGame(it)) }
            .onErr { failure ->
                Log.d(logTag, "Error: $failure")
                _uiState.update { state ->
                    state.copy(
                        isSubmitting = false,
                        warning = "Unable to save the game. Please try again.",
                    )
                }
            }
    }

    private fun showValidationFailure() {
        _uiState.update { state ->
            state.copy(
                validationStatus = ValidationStatus.Invalid,
                isEditing = true,
                isSubmitting = false,
                warning =
                    "This puzzle contains conflicting digits or has no solution. Please check the grid.",
            )
        }
    }
}
