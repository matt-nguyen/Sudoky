package com.nghianguyen.feature.scanner.camera.ui

import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nghianguyen.feature.scanner.R
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraAction
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraEvent
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraScreenState
import com.nghianguyen.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.SharedFlow

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
            modifier = Modifier.fillMaxSize().wrapContentSize().widthIn(max = 480.dp),
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
            val coordinateTransformer = remember { MutableCoordinateTransformer() }
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier =
                    Modifier.fillMaxSize().aspectRatio(1f).drawWithContent {
                        drawContent()

                        // drawing guide grid lines
                        val fullSize = size.height
                        val cellSize = fullSize / 9f
                        val thinWidth = 2f
                        val thickWidth = 5f
                        val lineColor = Color.White.copy(alpha = 0.8f)

                        for (i in 0..9) {
                            val offset = cellSize * i
                            val width = if (i % 3 == 0) thickWidth else thinWidth

                            // Horizontal
                            drawLine(lineColor, Offset(0f, offset), Offset(fullSize, offset), width)
                            // Vertical
                            drawLine(lineColor, Offset(offset, 0f), Offset(offset, fullSize), width)
                        }
                    },
                contentScale = ContentScale.FillWidth,
            )
            val spacing = LocalSpacing.current
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = spacing.xLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    modifier =
                        Modifier.size(80.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    onClick = { onAction(CameraAction.CapturePhoto) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.photo_camera_24px),
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Capture",
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))
                OutlinedButton(
                    onClick = { onScreenResult(CameraScreenResult.Exit) },
                    border = BorderStroke(1.dp, Color.White),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge, color = Color.White)
                }
            }
        }
    }
}
