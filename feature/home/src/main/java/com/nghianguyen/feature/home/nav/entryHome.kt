package com.nghianguyen.feature.home.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nghianguyen.feature.home.ui.HomeScreen
import com.nghianguyen.feature.home.ui.HomeScreenResult
import com.nghianguyen.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Registers the Home screen as a destination in the navigation graph.
 *
 * @param onHomeScreenResult A callback function that handles [HomeScreenResult]s,
 */
fun EntryProviderScope<NavKey>.entryHome(onHomeScreenResult: (HomeScreenResult) -> Unit) {
    entry<Home> {
        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        HomeScreen(
            state = state,
            event = viewModel.uiEvent,
            onAction = viewModel::handleAction,
            onHomeScreenResult = onHomeScreenResult,
        )
    }
}
