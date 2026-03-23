package com.nghianguyen.feature.sudoku.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nghianguyen.feature.sudoku.play.ui.PlayScreen
import com.nghianguyen.feature.sudoku.play.ui.PlayScreenResult
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun EntryProviderScope<NavKey>.entryPlay(onScreenResult: (PlayScreenResult) -> Unit) {
    entry<Play> { key ->
        val viewModel: PlayViewModel = koinViewModel { parametersOf(key.gameId) }
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        PlayScreen(
            state = state,
            event = viewModel.uiEvent,
            onAction = viewModel::handleAction,
            onScreenResult = onScreenResult,
        )
    }
}
