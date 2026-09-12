package com.nghianguyen.feature.scanner.camera.ui

import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraAction
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraEvent
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraScreenState
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

/**
 * Main screen for the camera scanner, managing permissions and hosting the preview.
 *
 * @param state Current UI state.
 * @param event Flow of one-time events.
 * @param onAction User interaction callback.
 * @param onScreenResult Navigation result callback.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(
    state: CameraScreenState,
    event: SharedFlow<CameraEvent>,
    onAction: (CameraAction) -> Unit,
    onScreenResult: (CameraScreenResult) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(state, event, onAction, onScreenResult)
    } else {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .wrapContentSize()
                    .widthIn(max = CameraPreviewConstants.PERMISSION_MAX_WIDTH),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val textToShow =
                if (cameraPermissionState.status.shouldShowRationale) {
                    "Camera permission is required to scan Sudoku puzzles."
                } else {
                    "Please grant camera permission to use the scanner."
                }
            Text(
                text = textToShow,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.medium))
            OutlinedButton(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text("Grant Permission")
            }
        }
    }
}

/**
 * The internal content of the camera scanner, displaying the viewfinder and capture controls.
 *
 * @param state Current UI state.
 * @param event Flow of one-time events.
 * @param onAction Callback for UI actions.
 * @param onScreenResult Callback for navigation results.
 */
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@Composable
fun CameraPreviewContent(
    state: CameraScreenState,
    event: SharedFlow<CameraEvent>,
    onAction: (CameraAction) -> Unit,
    onScreenResult: (CameraScreenResult) -> Unit,
) {

    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifeCycleOwner) {
        event.flowWithLifecycle(lifeCycleOwner.lifecycle).collect {
            when (it) {
                is CameraEvent.DigitsScanned -> {
                    onScreenResult(CameraScreenResult.ScannedDigits(it.scannedDigits))
                }
            }
        }
    }

    val surfaceRequest = state.surfaceRequest

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) { onAction(CameraAction.BindCamera(lifecycleOwner)) }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        surfaceRequest?.let { request ->
            CameraViewfinderWithGrid(surfaceRequest = request)
            val spacing = LocalSpacing.current
            CameraCaptureControls(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = spacing.xLarge),
                onCapture = { onAction(CameraAction.CapturePhoto) },
                onCancel = { onScreenResult(CameraScreenResult.Exit) },
            )
        }
    }
}
