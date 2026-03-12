package com.nghianguyen.sudoku

import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Sudoku game data.
 * Provides methods for retrieving, creating, updating, and deleting Sudoku games.
 */
interface SudokuGameRepository {

    /**
     * Retrieves all games that are currently in progress.
     * @return A [Result] containing a list of [SudokuGame]s or an [Error].
     */
    suspend fun getGameInProgress(): Result<List<SudokuGame>, Error>

    /**
     * Returns a [Flow] that emits the [SudokuGame] with the given [id].
     * Emits null if no game is found with the specified [id].
     * @param id The unique identifier of the game.
     * @return A [Flow] of [Result] containing the optional [SudokuGame].
     */
    fun getGame(id: Long): Flow<Result<SudokuGame?, Error>>

    /**
     * Creates a new Sudoku game with the provided [digitCells].
     * @param digitCells The list of cells that make up the initial game state.
     * @return A [Result] containing the ID of the newly created game, or an [Error].
     */
    suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error>

    /**
     * Updates an existing [SudokuGame].
     * @param game The game object containing updated state.
     * @return A [Result] indicating success or an [Error].
     */
    suspend fun updateGame(game: SudokuGame): Result<Unit, Error>

    /**
     * Deletes a [SudokuGame] from the repository.
     * @param game The game object to be deleted.
     * @return A [Result] indicating success or an [Error].
     */
    suspend fun deleteGame(game: SudokuGame): Result<Unit, Error>
}
