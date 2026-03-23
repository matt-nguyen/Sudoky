package com.nghianguyen.data.sudoku.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nghianguyen.data.sudoku.local.db.entity.GameEntity
import com.nghianguyen.data.sudoku.local.db.entity.GameWithCells
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Transaction @Query("SELECT * FROM game") suspend fun getGame(): List<GameWithCells>

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    fun getGame(id: Long): Flow<GameWithCells?>

    @Insert suspend fun insertGame(entity: GameEntity): Long

    @Delete suspend fun deleteGame(entity: GameEntity)
}
