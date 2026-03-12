package com.nghianguyen.data.sudoku.local

import com.nghianguyen.data.sudoku.GameLocalDataSource
import com.nghianguyen.data.sudoku.local.db.SudokuDatabase
import com.nghianguyen.data.sudoku.local.db.dao.CellDao
import com.nghianguyen.data.sudoku.local.db.dao.GameDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSudokuRoomModule = module {
    single<GameLocalDataSource> {
        GameLocalDataSourceImpl(get(), get())
    }
    single<GameDao> {
        get<SudokuDatabase>().gameDao()
    }
    single<CellDao> {
        get<SudokuDatabase>().cellDao()
    }
    single<SudokuDatabase> {
        SudokuDatabase.getInstance(androidContext())
    }
}

