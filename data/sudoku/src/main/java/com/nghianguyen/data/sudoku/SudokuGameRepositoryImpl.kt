package com.nghianguyen.data.sudoku

import android.util.Log
import com.github.michaelbull.result.Result
import com.nghianguyen.domain.model.Error
import com.nghianguyen.sudoku.SudokuGameRepository
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.SudokuGame
import kotlinx.coroutines.flow.Flow

class SudokuGameRepositoryImpl(
    private val localDataSource: GameLocalDataSource
): SudokuGameRepository {
    override suspend fun getGameInProgress(): Result<List<SudokuGame>, Error> {
        Log.d("SudokuGameRepositoryImpl", "getGameInProgress")
        return localDataSource.getGameInProgress()
    }

    override fun getGame(id: Long): Flow<Result<SudokuGame?, Error>> {
        Log.d("SudokuGameRepositoryImpl", "getGame: $id")
        return localDataSource.getGame(id)
    }

    override suspend fun newGame(digitCells: List<DigitCell>): Result<Long, Error> {
        Log.d("SudokuGameRepositoryImpl", "newGame")
        return localDataSource.newGame(digitCells)
    }

    override suspend fun updateGame(game: SudokuGame): Result<Unit, Error> {
        Log.d("SudokuGameRepositoryImpl", "updateGame: ${game.id}")
        return localDataSource.updateGame(game)
    }

    override suspend fun deleteGame(game: SudokuGame): Result<Unit, Error> {
        Log.d("SudokuGameRepositoryImpl", "deleteGame: ${game.id}")
        return localDataSource.deleteGame(game)
    }
}