package com.nghianguyen.feature.scanner.camera.viewmodel

import androidx.camera.core.SurfaceRequest

/**
 * Represents the UI state of the camera scanning screen.
 * @property surfaceRequest The [SurfaceRequest] used to display the camera preview.
 */
data class CameraScreenState(
    val surfaceRequest: SurfaceRequest?
)
