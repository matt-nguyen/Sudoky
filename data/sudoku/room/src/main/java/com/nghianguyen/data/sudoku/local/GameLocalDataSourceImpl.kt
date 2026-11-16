package com.nghianguyen.data.sudoku.local

import android.util.Log
import androidx.room.withTransaction
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.runCatching
import com.nghianguyen.data.sudoku.GameLocalDataSource
import com.nghianguyen.data.sudoku.local.db.SudokuDatabase
import com.nghianguyen.data.sudoku.local.db.dao.CellDao
import com.nghianguyen.data.sudoku.local.db.dao.GameDao
import com.nghianguyen.data.sudoku.local.db.entity.GameEntity
import com.nghianguyen.data.sudoku.local.db.entity.toDomain
import com.nghianguyen.data.sudoku.local.db.entity.toEntity
import com.nghianguyen.data.sudoku.local.ext.mapLocalDataError
import com.nghianguyen.domain.model.Error
import com.nghianguyen.domain.model.SudokuGameError
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GameLocalDataSourceImpl(
    private val gameDao: GameDao,
    private val cellDao: CellDao,
    private val database: SudokuDatabase,
) : GameLocalDataSource {
    override suspend fun getGameInProgress(): Result<List<SudokuGame>, Error> {
        Log.d("GameLocalDataSourceImpl", "getGameInProgress")
        return runCatching { gameDao.getGame().map { it.toDomain() } }.mapLocalDataError()
    }

    override fun getGame(id: Long): Flow<Result<SudokuGame?, Error>> {
        Log.d("GameLocalDataSourceImpl", "getGame: $id")
        return gameDao.getGame(id).map { Ok(it?.toDomain()) }
    }

    override suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error> {
        Log.d("GameLocalDataSourceImpl", "newGame")
        return runCatching {
                val newGameId = gameDao.insertGame(GameEntity())
                Log.d("GameLocalDataSourceImpl", "newGameId: $newGameId")
                cellDao.upsertCells(digitCells.toEntity(newGameId))
                newGameId
            }
            .mapLocalDataError()
    }

    override suspend fun updateGame(
        gameId: Long,
        transform: (SudokuGame) -> Result<SudokuGame, Error>,
    ): Result<Unit, Error> = runCatching {
        database.withTransaction {
//            val game = gameDao.getGameOnce(gameId)?.toDomain()
            val game = gameDao.getGame(gameId).firstOrNull()?.toDomain()
                ?: return@withTransaction Err(SudokuGameError.GameNotFound(gameId))
            transform(game).map { updated ->
                require(updated.id == gameId) { "A game update must preserve its identity" }
                cellDao.upsertCells(updated.cells.toEntity(gameId))
            }
        }
    }.mapLocalDataError().andThen { it }

    override suspend fun deleteGame(game: SudokuGame): Result<Unit, Error> {
        Log.d("GameLocalDataSourceImpl", "deleteGame: ${game.id}")
        return runCatching {
                cellDao.deleteCells(game.cells.toEntity(game.id))
                gameDao.deleteGame(GameEntity(id = game.id))
            }
            .mapLocalDataError()
    }
}
