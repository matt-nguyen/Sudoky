package com.nghianguyen.data.scanner.mlkit

import android.graphics.Bitmap
import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.nghianguyen.domain.model.SudokuScanError
import com.nghianguyen.sudoku.SudokuScanner
import com.nghianguyen.sudoku.model.ScannedDigitCell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class SudokuScannerImpl(
    private val textRecognizer: TextRecognizer,
    private val mapper: SudokuGridMapper = SudokuGridMapper()
) : SudokuScanner {
    override suspend fun scanForDigits(bitmap: Bitmap): Result<List<ScannedDigitCell>, SudokuScanError> {
        val text: Text? = suspendCancellableCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    continuation.resume(text)
                }
                .addOnFailureListener { e ->
                    Log.e("SudokuScannerImpl", "textRecognizer failed", e)
                    continuation.resume(null)
                }
        }

        if (text == null) {
            return Err(SudokuScanError.SCAN_FAILED)
        }

        return withContext(Dispatchers.Default) {
            val foundDigits = mapper.mapTextToGrid(text, bitmap.height)
            Ok(foundDigits)
        }
    }
}
