package com.nghianguyen.data.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow

/** Local data source for managing [SudokuGame] data. */
interface GameLocalDataSource {
    /** Returns a list of all games that are currently in progress. */
    suspend fun getGameInProgress(): Result<List<SudokuGame>, Error>

    /**
     * Returns a [Flow] that emits the [SudokuGame] with the given [id] whenever it changes.
     *
     * @param id The ID of the game to retrieve.
     */
    fun getGame(id: Long): Flow<Result<SudokuGame?, Error>>

    /**
     * Creates a new game with the given [digitCells] and returns its ID.
     *
     * @param digitCells The initial cells of the Sudoku grid.
     */
    suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error>

    /**
     * Updates an existing [game].
     *
     * @param game The game to update.
     */
    suspend fun updateGame(game: SudokuGame): Result<Unit, Error>

    /**
     * Deletes the specified [game].
     *
     * @param game The game to delete.
     */
    suspend fun deleteGame(game: SudokuGame): Result<Unit, Error>
}
