package com.nghianguyen.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error

/** Edits candidate notes using the latest persisted game state. */
class EditSudokuNotesUseCase(private val repository: SudokuGameRepository) {
    suspend operator fun invoke(
        gameId: Long,
        digit: Int,
        row: Int,
        col: Int,
    ): Result<Unit, Error> =
        repository.updateGame(gameId) { game -> game.editNotes(digit, row, col) }
}
