package com.nghianguyen.data.sudoku.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nghianguyen.sudoku.model.DigitCell

@Entity(
    tableName = "cell",
    indices =
        [
            Index(
                name = "unique_game_id_and_row_and_col",
                value = ["game_id", "row", "col"],
                unique = true,
            )
        ],
    foreignKeys =
        [ForeignKey(entity = GameEntity::class, parentColumns = ["id"], childColumns = ["game_id"])],
)
data class CellEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "game_id") val gameId: Long,
    @ColumnInfo(name = "current") val current: Int,
    @ColumnInfo(name = "solution") val solution: Int,
    @ColumnInfo(name = "row") val row: Int,
    @ColumnInfo(name = "col") val col: Int,
    @ColumnInfo(name = "is_given") val isGiven: Boolean,
)

fun List<CellEntity>.toDomain(): List<DigitCell> {
    return map { it.toDomain() }
}

fun CellEntity.toDomain(): DigitCell {
    return DigitCell(
        id = id,
        current = current,
        solution = solution,
        row = row,
        col = col,
        isGiven = isGiven,
    )
}

fun List<DigitCell>.toEntity(gameId: Long): List<CellEntity> {
    return map { it.toEntity(gameId) }
}

fun DigitCell.toEntity(gameId: Long): CellEntity {
    return CellEntity(
        id = id,
        gameId = gameId,
        current = current,
        solution = solution,
        row = row,
        col = col,
        isGiven = isGiven,
    )
}
