package com.nghianguyen.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error

class CreateSudokuGameUseCase(private val repository: SudokuGameRepository) {

    // TODO: add game metadata
    suspend operator fun invoke(puzzle: PreparedSudoku): Result<Long, Error> =
        repository.newGame(puzzle.cells)
}
