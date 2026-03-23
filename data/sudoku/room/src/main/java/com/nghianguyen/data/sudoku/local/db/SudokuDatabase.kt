package com.nghianguyen.data.sudoku.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nghianguyen.data.sudoku.local.db.dao.CellDao
import com.nghianguyen.data.sudoku.local.db.dao.GameDao
import com.nghianguyen.data.sudoku.local.db.entity.CellEntity
import com.nghianguyen.data.sudoku.local.db.entity.GameEntity

@Database(entities = [CellEntity::class, GameEntity::class], version = 1, exportSchema = true)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    abstract fun cellDao(): CellDao

    companion object {
        @Volatile private var INSTANCE: SudokuDatabase? = null

        fun getInstance(context: Context): SudokuDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: Room.databaseBuilder(
                                context.applicationContext,
                                SudokuDatabase::class.java,
                                "sudoku.db",
                            )
                            .build()
                            .also { INSTANCE = it }
                }
        }
    }
}
