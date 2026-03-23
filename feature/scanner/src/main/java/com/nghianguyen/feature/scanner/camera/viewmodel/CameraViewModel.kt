package com.nghianguyen.feature.scanner.camera.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.scanner.camera.CameraManager
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuScanner

/**
 * ViewModel for the camera scanning screen. Orchestrates the camera lifecycle, photo capture, and
 * the subsequent digit scanning process.
 *
 * @property cameraManager Manager for camera-related operations.
 * @property sudokuScanner Scanner for identifying Sudoku digits in images.
 */
class CameraViewModel(
    private val cameraManager: CameraManager,
    private val sudokuScanner: SudokuScanner,
) : BaseViewModel<CameraScreenState, CameraAction, CameraEvent>() {

    override fun buildInitialState(): CameraScreenState = CameraScreenState(null)

    override suspend fun onStart() {
        launch {
            cameraManager.surfaceRequest.collect { surfaceRequest ->
                updateState { copy(surfaceRequest = surfaceRequest) }
            }
        }
    }

    override fun handleAction(action: CameraAction) {
        Log.d("CameraViewModel", "handleAction: $action")
        when (action) {
            is CameraAction.BindCamera -> bindToCamera(action.lifecycleOwner)
            CameraAction.CapturePhoto -> takePicture()
        }
    }

    private val onImageCaptured: (Bitmap) -> Unit = {
        launch {
            sudokuScanner
                .scanForDigits(it)
                .onSuccess { foundDigits ->
                    foundDigits.forEach { (digit, row, col) ->
                        Log.d("CameraViewModel", "digit $digit, row $row, col $col")
                    }
                    sendEvent(CameraEvent.DigitsScanned(foundDigits))
                }
                .onFailure { Log.d("CameraViewModel", "scanForDigits onfailure: $it") }
        }
    }

    private fun bindToCamera(lifecycleOwner: LifecycleOwner) {
        launch { cameraManager.bindToLifecycle(lifecycleOwner, onImageCaptured) }
    }

    private fun takePicture() {
        cameraManager.takePicture()
    }
}
