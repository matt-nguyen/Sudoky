package com.nghianguyen.data.scanner

import org.koin.dsl.module

val dataScannerModule = module {
    single<SudokuGridImageNormalizer> { SudokuGridImageNormalizerImpl() }
}
