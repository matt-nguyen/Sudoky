package com.nghianguyen.feature.scanner.camera.ui

import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.nghianguyen.sudoku.model.BOX_SIZE
import com.nghianguyen.sudoku.model.GRID_SIZE

/**
 * Displays the camera viewfinder with the Sudoku-cell alignment grid drawn over it.
 *
 * @param surfaceRequest The request that supplies frames for the viewfinder.
 */
@Composable
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
fun CameraViewfinderWithGrid(surfaceRequest: SurfaceRequest) {
    val coordinateTransformer = remember { MutableCoordinateTransformer() }
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        coordinateTransformer = coordinateTransformer,
        modifier =
            Modifier.fillMaxSize().aspectRatio(1f).drawWithContent {
                drawContent()

                val fullSize = size.height
                val cellSize = fullSize / GRID_SIZE.toFloat()
                val thinWidth = CameraPreviewConstants.GRID_THIN_LINE_WIDTH
                val thickWidth = CameraPreviewConstants.GRID_THICK_LINE_WIDTH
                val lineColor = Color.White.copy(alpha = CameraPreviewConstants.GRID_LINE_ALPHA)

                for (i in 0..GRID_SIZE) {
                    val offset = cellSize * i
                    val width = if (i % BOX_SIZE == 0) thickWidth else thinWidth

                    drawLine(lineColor, Offset(0f, offset), Offset(fullSize, offset), width)
                    drawLine(lineColor, Offset(offset, 0f), Offset(offset, fullSize), width)
                }
            },
        contentScale = ContentScale.FillWidth,
    )
}
