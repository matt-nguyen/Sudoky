package com.nghianguyen.data.scanner.mlkit

import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nghianguyen.sudoku.SudokuScanner
import org.koin.dsl.module

val dataScannerMlKitModule = module {
    single<TextRecognizer> {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    single<SudokuScanner> {
        SudokuScannerImpl(get())
    }
}
