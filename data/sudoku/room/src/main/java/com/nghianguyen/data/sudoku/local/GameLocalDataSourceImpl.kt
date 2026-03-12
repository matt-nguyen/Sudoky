package com.nghianguyen.data.sudoku.local

import android.util.Log
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.nghianguyen.data.sudoku.GameLocalDataSource
import com.nghianguyen.data.sudoku.local.db.dao.CellDao
import com.nghianguyen.data.sudoku.local.db.dao.GameDao
import com.nghianguyen.data.sudoku.local.db.entity.GameEntity
import com.nghianguyen.data.sudoku.local.db.entity.toDomain
import com.nghianguyen.data.sudoku.local.db.entity.toEntity
import com.nghianguyen.data.sudoku.local.ext.mapLocalDataError
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameLocalDataSourceImpl(
    private val gameDao: GameDao,
    private val cellDao: CellDao
): GameLocalDataSource {
    override suspend fun getGameInProgress(): Result<List<SudokuGame>, Error> {
        Log.d("GameLocalDataSourceImpl", "getGameInProgress")
        return runCatching {
            gameDao.getGame().map { it.toDomain() }
        }.mapLocalDataError()
    }

    override fun getGame(id: Long): Flow<Result<SudokuGame?, Error>> {
        Log.d("GameLocalDataSourceImpl", "getGame: $id")
        return gameDao.getGame(id)
            .map {
                Ok(it?.toDomain())
            }
    }

    override suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error> {
        Log.d("GameLocalDataSourceImpl", "newGame")
        return runCatching {
            val newGameId = gameDao.insertGame(GameEntity())
            Log.d("GameLocalDataSourceImpl", "newGameId: $newGameId")
            cellDao.upsertCells(digitCells.toEntity(newGameId))
            newGameId
        }.mapLocalDataError()
    }

    override suspend fun updateGame(game: SudokuGame): Result<Unit, Error> {
        Log.d("GameLocalDataSourceImpl", "updateGame: ${game.id}")
        return runCatching {
            cellDao.upsertCells(game.cells.toEntity(game.id))
        }.mapLocalDataError()
    }

    override suspend fun deleteGame(game: SudokuGame): Result<Unit, Error> {
        Log.d("GameLocalDataSourceImpl", "deleteGame: ${game.id}")
        return runCatching {
            cellDao.deleteCells(game.cells.toEntity(game.id))
            gameDao.deleteGame(GameEntity(id = game.id))
        }.mapLocalDataError()
    }
}