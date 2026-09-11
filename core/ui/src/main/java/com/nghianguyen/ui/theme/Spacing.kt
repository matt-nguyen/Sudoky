package com.nghianguyen.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines a set of spacing values (in dp) to be used throughout the application UI.
 */
@Immutable
data class Spacing(
    val xSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
    val xLarge: Dp = 48.dp,
)

/**
 * A CompositionLocal used to provide [Spacing] throughout the UI hierarchy.
 */
val LocalSpacing = staticCompositionLocalOf { Spacing() }
