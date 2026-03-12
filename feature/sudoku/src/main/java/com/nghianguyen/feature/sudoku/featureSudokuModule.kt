package com.nghianguyen.feature.sudoku

import com.nghianguyen.feature.sudoku.play.viewmodel.PlayViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureSudokuModule = module {
    viewModel<PlayViewModel> { params ->
        PlayViewModel(params.get(), get())
    }
}