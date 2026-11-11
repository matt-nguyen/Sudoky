package com.nghianguyen.sudoku

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.nghianguyen.sudoku.model.ScannedDigitCell
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrepareScannedSudokuUseCaseTest {
    private val builder = SudokuGridBuilder()
    private val prepare = PrepareScannedSudokuUseCase(builder, SudokuSolver())

    @Test
    fun `preparation fills solutions and preserves scanned cells`() {
        val scanned = listOf(ScannedDigitCell("5", 0, 0))
        val puzzle = requireNotNull(prepare(scanned).get())

        assertEquals(builder.buildSudokuGrid(scanned), puzzle.cells.map { it.copy(solution = 0) })
        assertTrue(puzzle.cells.all { it.solution in 1..9 })
    }

    @Test
    fun `unsolvable puzzle retains original grid for preview`() {
        val scanned = (0..7).map { ScannedDigitCell("${it + 1}", 0, it) } +
            ScannedDigitCell("9", 1, 8)
        val failure = requireNotNull(prepare(scanned).getError())

        assertEquals(builder.buildSudokuGrid(scanned), failure.cells)
        assertEquals("Sudoku puzzle is unsolvable", failure.cause.message)
    }
}
