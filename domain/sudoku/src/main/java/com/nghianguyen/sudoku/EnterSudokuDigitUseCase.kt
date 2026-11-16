package com.nghianguyen.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error

/** Enters a digit using the latest persisted game state. */
class EnterSudokuDigitUseCase(private val repository: SudokuGameRepository) {
    suspend operator fun invoke(
        gameId: Long,
        digit: Int,
        row: Int,
        col: Int,
    ): Result<Unit, Error> = repository.updateGame(gameId) { game ->
        game.setDigit(digit, row, col)
    }
}
