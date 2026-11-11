package com.nghianguyen.sudoku

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSudokuGameRepository : SudokuGameRepository {
    var newGameError: Error? = null

    private val games = MutableStateFlow<Map<Long, SudokuGame>>(emptyMap())
    private var nextId = 1L

    override suspend fun getGameInProgress(): Result<List<SudokuGame>, Error> =
        Ok(games.value.values.toList())

    override fun getGame(id: Long): Flow<Result<SudokuGame?, Error>> =
        games.map { Ok(it[id]) }

    override suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error> {
        newGameError?.let { return Err(it) }
        val id = nextId++
        games.update { it + (id to SudokuGame(id, digitCells.toList())) }
        return Ok(id)
    }

    override suspend fun updateGame(game: SudokuGame): Result<Unit, Error> {
        games.update { if (game.id in it) it + (game.id to game) else it }
        return Ok(Unit)
    }

    override suspend fun deleteGame(game: SudokuGame): Result<Unit, Error> {
        games.update { it - game.id }
        return Ok(Unit)
    }
}
