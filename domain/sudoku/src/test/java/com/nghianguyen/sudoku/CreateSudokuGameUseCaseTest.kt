package com.nghianguyen.sudoku

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.get
import com.nghianguyen.domain.model.LocalDataError
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Column
import com.nghianguyen.sudoku.model.Digit
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.Row
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateSudokuGameUseCaseTest {
    private val puzzle = PreparedSudoku(
        listOf(
            "534678912",
            "672195348",
            "198342567",
            "859761423",
            "426853791",
            "713924856",
            "961537284",
            "287419635",
            "345286179",
        ).flatMapIndexed { row, digits ->
            digits.mapIndexed { col, digit ->
                DigitCell(
                    id = 0,
                    state =
                        if (col == 0) CellState.Given(Digit(digit.digitToInt())) else CellState.Empty,
                    solution = digit.digitToInt(),
                    row = Row(row),
                    col = Column(col),
                )
            }
        }
    )

    @Test
    fun `creation saves prepared cells and returns game id`() = runBlocking {
        val repository = FakeSudokuGameRepository()

        val id = requireNotNull(CreateSudokuGameUseCase(repository)(puzzle).get())
        val game = requireNotNull(repository.getGame(id).first().get())

        assertEquals(id, game.id)
        assertEquals(puzzle.cells, game.cells)
    }

    @Test
    fun `creation returns repository failure`() = runBlocking {
        val repository = FakeSudokuGameRepository().apply {
            newGameError = LocalDataError.DATABASE_ERROR
        }

        assertEquals(Err(LocalDataError.DATABASE_ERROR), CreateSudokuGameUseCase(repository)(puzzle))
        assertTrue(requireNotNull(repository.getGameInProgress().get()).isEmpty())
    }
}
