package com.nghianguyen.data.sudoku.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nghianguyen.data.sudoku.local.db.entity.toDomain
import com.nghianguyen.data.sudoku.local.db.entity.toEntity
import com.nghianguyen.sudoku.model.CellState
import com.nghianguyen.sudoku.model.Digit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises the generated Room migration and note mapping against an on-disk game. */
@RunWith(AndroidJUnit4::class)
class SudokuDatabaseMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun migrateGame_whenReopened_preservesDigitsAndPersistsNotes() = runBlocking {
        val name = "cell-notes-migration-test.db"
        context.deleteDatabase(name)
        try {
            createVersionOneDatabase(name)
            withDatabase(name) { database ->
                val cells =
                    requireNotNull(database.gameDao().getGame(1).first())
                        .toDomain()
                        .cells
                        .associateBy { it.col.value }
                assertEquals(CellState.Given(Digit(3)), cells.getValue(0).state)
                assertEquals(CellState.Entered(Digit(4)), cells.getValue(1).state)
                val empty = cells.getValue(2)
                assertEquals(CellState.Empty, empty.state)
                assertEquals(emptySet<Digit>(), empty.notes)
                database
                    .cellDao()
                    .updateCell(empty.copy(notes = setOf(Digit(1), Digit(9))).toEntity(1))
            }
            withDatabase(name) { database ->
                val game = requireNotNull(database.gameDao().getGame(1).first()).toDomain()
                assertEquals(
                    setOf(Digit(1), Digit(9)),
                    game.cells.single { it.col.value == 2 }.notes,
                )
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    private suspend fun withDatabase(name: String, block: suspend (SudokuDatabase) -> Unit) {
        val database = Room.databaseBuilder(context, SudokuDatabase::class.java, name).build()
        try {
            block(database)
        } finally {
            database.close()
        }
    }

    private fun createVersionOneDatabase(name: String) {
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { database ->
            database.execSQL("CREATE TABLE game (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            database.execSQL(
                "CREATE TABLE cell (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "game_id INTEGER NOT NULL, current INTEGER NOT NULL, solution INTEGER NOT NULL, " +
                    "row INTEGER NOT NULL, col INTEGER NOT NULL, is_given INTEGER NOT NULL, " +
                    "FOREIGN KEY(game_id) REFERENCES game(id) ON UPDATE NO ACTION ON DELETE NO ACTION)",
            )
            database.execSQL(
                "CREATE UNIQUE INDEX unique_game_id_and_row_and_col ON cell (game_id, row, col)",
            )
            database.execSQL("INSERT INTO game (id) VALUES (1)")
            insertVersionOneCells(database)
            database.version = 1
        }
    }

    private fun insertVersionOneCells(database: SQLiteDatabase) {
        database.execSQL(
            "INSERT INTO cell (id, game_id, current, solution, row, col, is_given) VALUES " +
                "(1, 1, 3, 3, 0, 0, 1), (2, 1, 4, 5, 0, 1, 0), (3, 1, 0, 7, 0, 2, 0)",
        )
    }
}
