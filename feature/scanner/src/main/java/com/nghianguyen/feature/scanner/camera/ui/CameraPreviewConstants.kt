package com.nghianguyen.feature.scanner.camera.ui

import androidx.compose.ui.unit.dp

/** Constants defining dimensions and styles for the camera preview UI. */
internal object CameraPreviewConstants {
    /** Maximum width for the permission rationale text. */
    val PERMISSION_MAX_WIDTH = 480.dp

    /** Width of thin lines in the guide grid. */
    const val GRID_THIN_LINE_WIDTH = 2f

    /** Width of thick lines in the guide grid. */
    const val GRID_THICK_LINE_WIDTH = 5f

    /** Transparency level for the guide grid lines. */
    const val GRID_LINE_ALPHA = 0.8f

    /** Size of the circular capture button. */
    val CAPTURE_BUTTON_SIZE = 80.dp

    /** Size of the camera icon inside the capture button. */
    val CAPTURE_ICON_SIZE = 36.dp

    /** Width of borders used for outlined buttons. */
    val BUTTON_BORDER_WIDTH = 1.dp

    /** Corner radius for the cancel button. */
    val CANCEL_CORNER_RADIUS = 8.dp
}
