package com.nghianguyen.feature.scanner.camera.viewmodel

import androidx.lifecycle.LifecycleOwner

/** Represents actions that can be performed on the camera scanning screen. */
sealed interface CameraAction {
    /**
     * Binds the camera to the specified [lifecycleOwner].
     *
     * @property lifecycleOwner The lifecycle owner to bind the camera to.
     */
    data class BindCamera(val lifecycleOwner: LifecycleOwner) : CameraAction

    /** Requests the camera to capture a photo for digit scanning. */
    data object CapturePhoto : CameraAction
}
