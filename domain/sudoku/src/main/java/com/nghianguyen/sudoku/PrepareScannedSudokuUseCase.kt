package com.nghianguyen.sudoku

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.ScannedDigitCell

data class PreparedSudoku(val cells: List<DigitCell>)
data class PreparedSudokuFailure(val cells: List<DigitCell>, val cause: Throwable)

/** A puzzle whose cells have been prepared and solved for game creation. */
class PrepareScannedSudokuUseCase(
    private val gridBuilder: SudokuGridBuilder,
    private val solver: SudokuSolver,
) {
    operator fun invoke(
        scannedDigits: List<ScannedDigitCell>
    ): Result<PreparedSudoku, PreparedSudokuFailure> {
        val cells = gridBuilder.buildSudokuGrid(scannedDigits)
        return solver.solveBoard(cells).fold(
            onSuccess = { Ok(PreparedSudoku(it)) },
            onFailure = { Err(PreparedSudokuFailure(cells, it)) },
        )
    }
}
