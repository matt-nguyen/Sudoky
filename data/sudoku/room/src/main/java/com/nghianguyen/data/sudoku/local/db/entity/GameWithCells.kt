package com.nghianguyen.data.sudoku.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.nghianguyen.sudoku.model.SudokuGame

data class GameWithCells(
    @Embedded val gameEntity: GameEntity,
    @Relation(parentColumn = "id", entityColumn = "game_id") val cellEntities: List<CellEntity>,
)

fun GameWithCells.toDomain(): SudokuGame {
    return SudokuGame(id = gameEntity.id, cells = cellEntities.toDomain())
}
