package com.nghianguyen.sudoku.model

import org.junit.Assert.assertThrows
import org.junit.Test

class DigitCellTest {
    @Test
    fun `filled cells reject notes when constructed or copied`() {
        val notes = setOf(Digit(2))
        val emptyCell = DigitCell(1, CellState.Empty, 7, Row(0), Column(0), notes)

        assertThrows(IllegalArgumentException::class.java) {
            DigitCell(1, CellState.Given(Digit(7)), 7, Row(0), Column(0), notes)
        }
        assertThrows(IllegalArgumentException::class.java) {
            emptyCell.copy(state = CellState.Entered(Digit(7)))
        }
    }
}
