package com.nghianguyen.data.scanner.mlkit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.nghianguyen.data.scanner.SudokuGridImageNormalizer
import com.nghianguyen.domain.model.SudokuScanError
import com.nghianguyen.sudoku.SudokuScanner
import com.nghianguyen.sudoku.model.ScanImage
import com.nghianguyen.sudoku.model.ScannedDigitCell
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of [SudokuScanner] that uses Google's ML Kit Text Recognition to identify digits
 * in a Sudoku grid. Captured images are decoded and normalized before text recognition.
 *
 * @property textRecognizer The ML Kit [TextRecognizer] used to extract text from the image.
 * @property imageNormalizer The [SudokuGridImageNormalizer] used to crop, scale, and rotate the image.
 * @property mapper The [SudokuGridMapper] used to map detected text blocks to Sudoku grid coordinates.
 */
class SudokuScannerImpl(
    private val textRecognizer: TextRecognizer,
    private val imageNormalizer: SudokuGridImageNormalizer,
    private val mapper: SudokuGridMapper = SudokuGridMapper(),
) : SudokuScanner {
    override suspend fun scanForDigits(
        image: ScanImage
    ): Result<List<ScannedDigitCell>, SudokuScanError> = withContext(Dispatchers.Default) {
        var decoded: Bitmap? = null
        var normalized: Bitmap? = null
        try {
            val source = BitmapFactory.decodeByteArray(
                image.encodedBytes, 0, image.encodedBytes.size
            ) ?: return@withContext Err(SudokuScanError.SCAN_FAILED)
            decoded = source
            val grid = imageNormalizer.normalize(
                source = source,
                rotationDegrees = image.rotationDegrees.toFloat(),
                targetSizePx = minOf(source.width, source.height).toFloat(),
            )
            normalized = grid
            // Await completion even on cancellation: ML Kit still owns the bitmap until then.
            val text: Text = suspendCoroutine { continuation ->
                textRecognizer.process(InputImage.fromBitmap(grid, 0))
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
                    .addOnCanceledListener { continuation.resumeWithException(CancellationException()) }
            }
            Ok(mapper.mapTextToGrid(text, grid.height))
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e("SudokuScannerImpl", "Image scan failed", exception)
            Err(SudokuScanError.SCAN_FAILED)
        } finally {
            if (normalized !== decoded) normalized?.recycle()
            decoded?.recycle()
        }
    }
}
