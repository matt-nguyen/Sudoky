package com.nghianguyen.feature.scanner.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.nghianguyen.feature.scanner.camera.ui.CameraPreviewScreen
import com.nghianguyen.feature.scanner.camera.ui.CameraScreenResult
import com.nghianguyen.feature.scanner.camera.viewmodel.CameraViewModel
import com.nghianguyen.feature.scanner.confirm.ui.ConfirmScreen
import com.nghianguyen.feature.scanner.confirm.ui.ConfirmScreenResult
import com.nghianguyen.feature.scanner.confirm.viewmodel.ConfirmViewModel
import com.nghianguyen.feature.scanner.viewmodel.SharedScanViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed interface ScannerNavGraphResult {
    data class ContinueGame(val gameId: Long) : ScannerNavGraphResult

    data object Exit : ScannerNavGraphResult
}

fun EntryProviderScope<NavKey>.entryScanner(
    onScannerNavGraphResult: (ScannerNavGraphResult) -> Unit
) {
    entry<Scanner> {
        val sharedScanViewModel: SharedScanViewModel = koinViewModel()

        val scanBackStack = rememberNavBackStack(Scanner.Camera)
        NavDisplay(
            backStack = scanBackStack,
            modifier = Modifier.fillMaxSize(),
            entryProvider =
                entryProvider {
                    entryScannerCamera(scanBackStack, sharedScanViewModel, onScannerNavGraphResult)
                    entryScannerConfirm(scanBackStack, sharedScanViewModel, onScannerNavGraphResult)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.entryScannerCamera(
    scanBackStack: NavBackStack<NavKey>,
    sharedScanViewModel: SharedScanViewModel,
    onScannerNavGraphResult: (ScannerNavGraphResult) -> Unit,
) {
    entry<Scanner.Camera> {
        val viewModel: CameraViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        CameraPreviewScreen(state, viewModel.uiEvent, viewModel::handleAction) { cameraScreenResult
            ->
            when (cameraScreenResult) {
                is CameraScreenResult.ScannedDigits -> {
                    sharedScanViewModel.setScannedDigits(cameraScreenResult.digits)
                    scanBackStack.add(Scanner.Confirm)
                }
                CameraScreenResult.Exit -> {
                    onScannerNavGraphResult(ScannerNavGraphResult.Exit)
                }
            }
        }
    }
}

fun EntryProviderScope<NavKey>.entryScannerConfirm(
    scanBackStack: NavBackStack<NavKey>,
    sharedScanViewModel: SharedScanViewModel,
    onScannerNavGraphResult: (ScannerNavGraphResult) -> Unit,
) {
    entry<Scanner.Confirm> {
        val scannedDigits by sharedScanViewModel.scannedDigits.collectAsStateWithLifecycle()

        val confirmViewModel: ConfirmViewModel = koinViewModel()
        val state by confirmViewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(scannedDigits) { confirmViewModel.setScannedDigits(scannedDigits) }
        ConfirmScreen(
            state = state,
            event = confirmViewModel.uiEvent,
            onAction = confirmViewModel::handleAction,
        ) { confirmScreenResult ->
            when (confirmScreenResult) {
                ConfirmScreenResult.Redo -> {
                    scanBackStack.remove(Scanner.Confirm)
                }
                is ConfirmScreenResult.ConfirmedGame -> {
                    onScannerNavGraphResult(
                        ScannerNavGraphResult.ContinueGame(confirmScreenResult.gameId)
                    )
                }
                ConfirmScreenResult.Exit -> {
                    onScannerNavGraphResult(ScannerNavGraphResult.Exit)
                }
            }
        }
    }
}
