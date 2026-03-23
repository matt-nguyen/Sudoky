package com.nghianguyen.feature.scanner

import com.nghianguyen.feature.scanner.camera.CameraManager
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraViewModel
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmViewModel
import com.nghianguyen.feature.scanner.viewmodel.SharedScanViewModel
import com.nghianguyen.sudoku.SudokuSolver
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureScannerModule = module {
    single<CameraManager> { CameraManager(androidContext(), get()) }
    single<SudokuSolver> { SudokuSolver() }
    viewModel<CameraViewModel> { CameraViewModel(get(), get()) }
    viewModel<SharedScanViewModel> { SharedScanViewModel() }
    viewModel<ConfirmViewModel> { ConfirmViewModel(get(), get()) }
}
