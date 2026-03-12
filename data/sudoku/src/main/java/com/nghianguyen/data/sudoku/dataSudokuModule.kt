package com.nghianguyen.data.sudoku

import com.nghianguyen.sudoku.SudokuGameRepository
import org.koin.dsl.module

val dataSudokuModule = module {
    single<SudokuGameRepository>{
        SudokuGameRepositoryImpl(get())
    }
}