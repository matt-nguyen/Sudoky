package com.nghianguyen.data.sudoku.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Column
import com.nghianguyen.sudoku.model.Digit
import com.nghianguyen.sudoku.model.DigitCell
import com.nghianguyen.sudoku.model.EMPTY_CELL_VALUE
import com.nghianguyen.sudoku.model.Row
import com.nghianguyen.sudoku.model.digit

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
        state =
            when {
                current == EMPTY_CELL_VALUE -> CellState.Empty
                isGiven -> CellState.Given(Digit(current))
                else -> CellState.Entered(Digit(current))
            },
        solution = solution,
        row = Row(row),
        col = Column(col),
    )
}

fun List<DigitCell>.toEntity(gameId: Long): List<CellEntity> {
    return map { it.toEntity(gameId) }
}

fun DigitCell.toEntity(gameId: Long): CellEntity {
    return CellEntity(
        id = id,
        gameId = gameId,
        current = state.digit?.value ?: EMPTY_CELL_VALUE,
        solution = solution,
        row = row.value,
        col = col.value,
        isGiven = state is CellState.Given,
    )
}
