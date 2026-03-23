package com.nghianguyen.data.sudoku.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game")
data class GameEntity(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0)
