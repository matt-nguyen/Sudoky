package com.nghianguyen.feature.scanner.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Scanner: NavKey {
    @Serializable
    data object Camera: NavKey

    @Serializable
    data object Confirm: NavKey
}