package com.nghianguyen.data.sudoku.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nghianguyen.data.sudoku.local.db.entity.CellEntity

@Dao
interface CellDao {

    @Query("SELECT * FROM cell WHERE game_id = :gameId")
    suspend fun getCellsForGame(gameId: Int): List<CellEntity>

//    @Insert
//    suspend fun insertCells(cells: List<CellEntity>)

    @Upsert
    suspend fun upsertCells(cells: List<CellEntity>)

    @Update
    suspend fun updateCell(cell: CellEntity)

    @Delete
    suspend fun deleteCells(cells: List<CellEntity>)
}