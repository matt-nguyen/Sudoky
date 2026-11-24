package com.nghianguyen.feature.sudoku.play.viewmodel

import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.EditSudokuNotesUseCase
import com.nghianguyen.sudoku.EnterSudokuDigitUseCase
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Column
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.Row
import com.nghianguyen.sudoku.model.SudokuGame
import com.nghianguyen.ui.component.sudokugrid.DigitCellItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `queued keypad edits retain their selection and mode`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = HeldGameRepository()
            val viewModel =
                PlayViewModel(
                    1L,
                    repository,
                    EnterSudokuDigitUseCase(repository),
                    EditSudokuNotesUseCase(repository),
                )
            backgroundScope.launch { viewModel.uiState.collect {} }
            try {
                viewModel.handleAction(PlayAction.OnDigitEntered(9))
                runCurrent()
                assertFalse(repository.started.isCompleted)

                viewModel.handleAction(PlayAction.OnCellSelected(0, 0))
                viewModel.handleAction(PlayAction.OnNotesModeToggled)
                viewModel.handleAction(PlayAction.OnDigitEntered(2))
                runCurrent()
                assertTrue(repository.started.isCompleted)

                viewModel.handleAction(PlayAction.OnDigitEntered(7))
                viewModel.handleAction(PlayAction.OnCellSelected(0, 1))
                viewModel.handleAction(PlayAction.OnNotesModeToggled)
                viewModel.handleAction(PlayAction.OnNotesModeToggled)
                viewModel.handleAction(PlayAction.OnNotesModeToggled)
                viewModel.handleAction(PlayAction.OnDigitEntered(7))
                repository.release.complete(Unit)
                runCurrent()

                val state = viewModel.uiState.value
                assertEquals(0 to 1, state.selectedCell)
                assertFalse(state.notesMode)
                assertEquals(listOf(DigitCellItem(2, 0, 0)), state.sudokuGridState.noteItems)
                assertEquals(listOf(DigitCellItem(7, 0, 1)), state.sudokuGridState.correctItems)
            } finally {
                viewModel.viewModelScope.cancel()
            }
        }

    private class HeldGameRepository : SudokuGameRepository {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        private val game =
            MutableStateFlow(
                SudokuGame(
                    1L,
                    List(2) { col ->
                        DigitCell(col.toLong(), CellState.Empty, 7, Row(0), Column(col))
                    },
                ),
            )

        override fun getGame(id: Long): Flow<Result<SudokuGame?, Error>> = game.map { Ok(it) }

        override suspend fun updateGame(
            gameId: Long,
            transform: (SudokuGame) -> Result<SudokuGame, Error>,
        ): Result<Unit, Error> {
            started.complete(Unit)
            release.await()
            return transform(game.value).map { game.value = it }
        }

        override suspend fun getGameInProgress(): Result<List<SudokuGame>, Error> =
            Ok(listOf(game.value))

        override suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error> =
            error("The test uses an existing game")

        override suspend fun deleteGame(game: SudokuGame): Result<Unit, Error> =
            error("The test does not delete games")
    }
}
