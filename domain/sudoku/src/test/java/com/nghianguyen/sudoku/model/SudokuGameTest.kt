package com.nghianguyen.sudoku.model

import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrThrow
import com.nghianguyen.domain.model.SudokuGameError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuGameTest {
    @Test
    fun `notes toggle and clear without entering an answer`() {
        val game = SudokuGame(1, listOf(cell(0, 0)))

        val edited =
            game
                .editNotes(2, 0, 0)
                .getOrThrow { AssertionError(it) }
                .editNotes(7, 0, 0)
                .getOrThrow { AssertionError(it) }
                .editNotes(2, 0, 0)
                .getOrThrow { AssertionError(it) }

        assertEquals(setOf(Digit(7)), edited.cells.single().notes)
        assertEquals(CellState.Empty, edited.cells.single().state)
        assertFalse(edited.isFinished())
        assertTrue(
            edited
                .editNotes(0, 0, 0)
                .getOrThrow { AssertionError(it) }
                .cells
                .single()
                .notes
                .isEmpty(),
        )
    }

    @Test
    fun `correct answers remove matching notes only from peers`() {
        val notes = setOf(Digit(2), Digit(7))
        val cells = listOf(cell(0, 0), cell(0, 8), cell(8, 0), cell(1, 1), cell(8, 8))
        val game = SudokuGame(1, cells.map { it.copy(notes = notes) })

        val edited = game.setDigit(7, 0, 0).getOrThrow { AssertionError(it) }

        assertEquals(CellState.Entered(Digit(7)), edited.cells.first().state)
        assertEquals(
            listOf(emptySet(), setOf(Digit(2)), setOf(Digit(2)), setOf(Digit(2)), notes),
            edited.cells.map { it.notes },
        )
    }

    @Test
    fun `incorrect answers preserve peer notes and can be cleared`() {
        val notes = setOf(Digit(2))
        val game =
            SudokuGame(1, listOf(cell(0, 0).copy(notes = notes), cell(0, 1).copy(notes = notes)))

        val answered = game.setDigit(2, 0, 0).getOrThrow { AssertionError(it) }
        assertEquals(notes, answered.cells[1].notes)
        assertEquals(answered, answered.editNotes(7, 0, 0).getOrThrow { AssertionError(it) })

        val cleared = answered.setDigit(0, 0, 0).getOrThrow { AssertionError(it) }
        assertEquals(CellState.Empty, cleared.cells.first().state)
        assertTrue(cleared.cells.first().notes.isEmpty())
    }

    @Test
    fun `notes reject invalid digits and coordinates and protect clues`() {
        val game = SudokuGame(1, listOf(cell(0, 0).copy(state = CellState.Given(Digit(7)))))

        assertEquals(SudokuGameError.InvalidDigit(10), game.editNotes(10, 0, 0).getError())
        assertEquals(SudokuGameError.InvalidCoordinate(0, 1), game.editNotes(2, 0, 1).getError())
        assertEquals(
            SudokuGameError.GivenCellModification(0, 0),
            game.editNotes(2, 0, 0).getError(),
        )
        assertEquals(SudokuGameError.GivenCellModification(0, 0), game.setDigit(0, 0, 0).getError())
    }

    @Test
    fun `final answer completes a grid that previously had only a candidate in that cell`() {
        val cells =
            List(TOTAL_CELLS) { index ->
                val cell = cell(index / GRID_SIZE, index % GRID_SIZE)
                if (index == 0) cell.copy(notes = setOf(Digit(7)))
                else cell.copy(state = CellState.Given(Digit(7)))
            }
        val game = SudokuGame(1, cells)

        assertFalse(game.isFinished())
        assertTrue(game.setDigit(7, 0, 0).getOrThrow { AssertionError(it) }.isFinished())
    }

    private fun cell(row: Int, col: Int) = DigitCell(0, CellState.Empty, 7, Row(row), Column(col))
}
