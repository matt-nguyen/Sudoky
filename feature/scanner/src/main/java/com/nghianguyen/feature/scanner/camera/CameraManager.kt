package com.nghianguyen.feature.scanner.camera

import android.content.Context
import android.graphics.Bitmap
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
import com.nghianguyen.data.scanner.ImagePreprocessor
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages CameraX operations for the Sudoku scanner.
 * Handles binding use cases to a lifecycle, providing a surface request for previews,
 * and capturing photos.
 *
 * @property context Application context.
 * @property imagePreprocessor Utility for preprocessing captured images.
 */
class CameraManager(
    private val context: Context,
    private val imagePreprocessor: ImagePreprocessor
) {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    /**
     * Flow emitting [SurfaceRequest]s for the camera preview.
     */
    val surfaceRequest = _surfaceRequest.asStateFlow()

    /**
     * Callback triggered when a photo is captured and preprocessed.
     */
    lateinit var onImageCaptured: (Bitmap) -> Unit

    private lateinit var imageCaptureUseCase: ImageCapture

    /**
     * Binds the camera use cases (Preview and ImageCapture) to the specified [lifecycleOwner].
     *
     * @param lifecycleOwner The lifecycle owner to bind to.
     * @param callback The function to call when an image is captured.
     */
    suspend fun bindToLifecycle(lifecycleOwner: LifecycleOwner, callback: (Bitmap) -> Unit) {
        onImageCaptured = callback

        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        processCameraProvider.unbindAll()

        processCameraProvider.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            useCaseGroup = buildUseCaseGroup()
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    /**
     * Triggers a photo capture. The result will be processed and returned via the
     * [onImageCaptured] callback.
     */
    fun takePicture() {
        imageCaptureUseCase.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val outputBitmap = imagePreprocessor.process(
                        source = image.toBitmap(),
                        rotationDegrees = image.imageInfo.rotationDegrees.toFloat(),
                        targetSizePx = image.height.toFloat()
                    )
                    onImageCaptured(outputBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    // TODO handle error
                }
            }
        )
    }

    private fun buildUseCaseGroup(): UseCaseGroup {
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
            .build()

        val previewUseCase = buildPreviewUseCase(resolutionSelector)
        imageCaptureUseCase = buildImageCaptureUseCase(resolutionSelector)

        val viewPort = ViewPort.Builder(
            Rational(1, 1),
            previewUseCase.targetRotation
        )
            .setScaleType(ViewPort.FILL_CENTER)
            .build()

        return UseCaseGroup.Builder()
            .addUseCase(previewUseCase).addUseCase(imageCaptureUseCase)
            .setViewPort(viewPort)
            .build()
    }

    private fun buildImageCaptureUseCase(resolutionSelector: ResolutionSelector): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setResolutionSelector(resolutionSelector)
            .build()
    }

    private fun buildPreviewUseCase(resolutionSelector: ResolutionSelector): Preview {
        return Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
            .apply {
                setSurfaceProvider { surfaceRequest ->
                    _surfaceRequest.value = surfaceRequest
                }
            }
    }

}