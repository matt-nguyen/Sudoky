package com.nghianguyen.feature.scanner.camera

import android.content.Context
import android.graphics.ImageFormat
import android.util.Rational
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.nghianguyen.sudoku.model.ScanImage
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages CameraX operations for the Sudoku scanner. Handles binding use cases to a lifecycle,
 * providing a surface request for previews, and capturing photos.
 *
 * @property context Application context.
 */
class CameraManager(private val context: Context) {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    /** Flow emitting [SurfaceRequest]s for the camera preview. */
    val surfaceRequest = _surfaceRequest.asStateFlow()

    /** Callback triggered when a photo is captured and copied into a [ScanImage]. */
    lateinit var onImageCaptured: (ScanImage) -> Unit

    private lateinit var onCaptureError: (Exception) -> Unit

    private lateinit var imageCaptureUseCase: ImageCapture

    /**
     * Binds the camera use cases (Preview and ImageCapture) to the specified [lifecycleOwner].
     *
     * @param lifecycleOwner The lifecycle owner to bind to.
     * @param callback The function to call when an image is captured.
     * @param onError Receives capture and JPEG extraction failures.
     */
    suspend fun bindToLifecycle(
        lifecycleOwner: LifecycleOwner,
        callback: (ScanImage) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        onImageCaptured = callback
        onCaptureError = onError

        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        processCameraProvider.unbindAll()

        processCameraProvider.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            useCaseGroup = buildUseCaseGroup(),
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    /**
     * Triggers a photo capture. The JPEG bytes and rotation are returned via the [onImageCaptured]
     * callback.
     */
    fun takePicture() {
        imageCaptureUseCase.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val captured =
                        try {
                            ScanImage(image.toJpegBytes(), image.imageInfo.rotationDegrees)
                        } catch (exception: Exception) {
                            onCaptureError(exception)
                            return
                        } finally {
                            image.close()
                        }
                    onImageCaptured(captured)
                }

                override fun onError(exception: ImageCaptureException) {
                    onCaptureError(exception)
                }
            },
        )
    }

    private fun buildUseCaseGroup(): UseCaseGroup {
        val resolutionSelector =
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .build()

        val previewUseCase = buildPreviewUseCase(resolutionSelector)
        imageCaptureUseCase = buildImageCaptureUseCase(resolutionSelector)

        val viewPort =
            ViewPort.Builder(Rational(1, 1), previewUseCase.targetRotation)
                .setScaleType(ViewPort.FILL_CENTER)
                .build()

        return UseCaseGroup.Builder()
            .addUseCase(previewUseCase)
            .addUseCase(imageCaptureUseCase)
            .setViewPort(viewPort)
            .build()
    }

    private fun buildImageCaptureUseCase(resolutionSelector: ResolutionSelector): ImageCapture {
        return ImageCapture.Builder()
            .setOutputFormat(ImageCapture.OUTPUT_FORMAT_JPEG)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setResolutionSelector(resolutionSelector)
            .build()
    }

    private fun buildPreviewUseCase(resolutionSelector: ResolutionSelector): Preview {
        return Preview.Builder().setResolutionSelector(resolutionSelector).build().apply {
            setSurfaceProvider { surfaceRequest -> _surfaceRequest.value = surfaceRequest }
        }
    }
}

private fun ImageProxy.toJpegBytes(): ByteArray {
    require(format == ImageFormat.JPEG) { "Expected JPEG ImageProxy; received format=$format" }
    val buffer = planes.single().buffer.duplicate()
    buffer.rewind()
    return ByteArray(buffer.remaining()).also(buffer::get)
}
