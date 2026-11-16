package com.nghianguyen.feature.sudoku

import com.nghianguyen.feature.sudoku.play.viewmodel.PlayViewModel
import com.nghianguyen.sudoku.EnterSudokuDigitUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureSudokuModule = module {
    factory { EnterSudokuDigitUseCase(get()) }
    viewModel<PlayViewModel> { params -> PlayViewModel(params.get(), get(), get()) }
}
