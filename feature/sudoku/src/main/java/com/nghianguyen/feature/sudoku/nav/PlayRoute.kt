package com.nghianguyen.feature.sudoku.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class Play(val gameId: Long): NavKey