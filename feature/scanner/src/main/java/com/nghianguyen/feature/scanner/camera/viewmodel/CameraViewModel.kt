package com.nghianguyen.feature.scanner.camera.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.scanner.camera.CameraManager
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuScanner
import com.nghianguyen.sudoku.model.ScanImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
) : BaseViewModel<CameraAction, CameraEvent>() {
    private val logTag = "CameraViewModel"

    private val _uiState = MutableStateFlow(CameraScreenState(null))
    val uiState =
        combine(_uiState, cameraManager.surfaceRequest) { state, surfaceRequest ->
            state.copy(surfaceRequest = surfaceRequest)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _uiState.value)

    override fun handleAction(action: CameraAction) {
        Log.d(logTag, "handleAction: $action")
        when (action) {
            is CameraAction.BindCamera -> bindToCamera(action.lifecycleOwner)
            CameraAction.CapturePhoto -> takePicture()
        }
    }

    private val onImageCaptured: (ScanImage) -> Unit = {
        launch {
            sudokuScanner
                .scanForDigits(it)
                .onSuccess { foundDigits ->
                    foundDigits.forEach { (digit, row, col) ->
                        Log.d(logTag, "digit $digit, row $row, col $col")
                    }
                    sendEvent(CameraEvent.DigitsScanned(foundDigits))
                }
                .onFailure { Log.d(logTag, "scanForDigits onfailure: $it") }
        }
    }

    private fun bindToCamera(lifecycleOwner: LifecycleOwner) {
        launch {
            cameraManager.bindToLifecycle(lifecycleOwner, onImageCaptured) { exception ->
                Log.e(logTag, "Image capture failed", exception)
            }
        }
    }

    private fun takePicture() {
        cameraManager.takePicture()
    }
}
