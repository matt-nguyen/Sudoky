package com.nghianguyen.feature.scanner.camera.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.nghianguyen.feature.scanner.R
import com.nghianguyen.ui.theme.LocalSpacing

/**
 * Displays the camera capture button and the cancellation control.
 *
 * @param modifier Modifier applied to the controls container.
 * @param onCapture Callback invoked when the user captures a photo.
 * @param onCancel Callback invoked when the user cancels scanning.
 */
@Composable
fun CameraCaptureControls(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit,
    onCancel: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier =
                Modifier.size(CameraPreviewConstants.CAPTURE_BUTTON_SIZE)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            onClick = onCapture,
        ) {
            Icon(
                painter = painterResource(R.drawable.photo_camera_24px),
                modifier = Modifier.size(CameraPreviewConstants.CAPTURE_ICON_SIZE),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Capture",
            )
        }

        Spacer(modifier = Modifier.height(spacing.medium))
        OutlinedButton(
            onClick = onCancel,
            border = BorderStroke(CameraPreviewConstants.BUTTON_BORDER_WIDTH, Color.White),
            shape = RoundedCornerShape(CameraPreviewConstants.CANCEL_CORNER_RADIUS),
        ) {
            Text("Cancel", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
    }
}
