package com.nghianguyen.sudoky

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nghianguyen.feature.home.nav.entryHome
import com.nghianguyen.feature.home.ui.HomeScreenResult
import com.nghianguyen.feature.scanner.nav.Scanner
import com.nghianguyen.feature.scanner.nav.ScannerNavGraphResult
import com.nghianguyen.feature.scanner.nav.entryScanner
import com.nghianguyen.feature.sudoku.nav.Play
import com.nghianguyen.feature.sudoku.nav.entryPlay
import com.nghianguyen.feature.sudoku.play.ui.PlayScreenResult

@Composable
fun NavigationRoot(startRoute: NavKey) {
    val rootBackStack = rememberNavBackStack(startRoute)
    NavDisplay(
        backStack = rootBackStack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                entryHome { homeScreenResult ->
                    Log.d("NavigationRoot", "homeScreenResult: $homeScreenResult")
                    when (homeScreenResult) {
                        HomeScreenResult.ScanSudoku -> {
                            rootBackStack.add(Scanner)
                        }

                        is HomeScreenResult.ContinueGame -> {
                            rootBackStack.add(Play(homeScreenResult.gameId))
                        }
                    }
                }
                entryScanner { scannerNavGraphResult ->
                    Log.d("NavigationRoot", "scannerNavGraphResult: $scannerNavGraphResult")
                    rootBackStack.removeLastOrNull()

                    when (scannerNavGraphResult) {
                        is ScannerNavGraphResult.ContinueGame -> {
                            rootBackStack.add(Play(scannerNavGraphResult.gameId))
                        }
                        ScannerNavGraphResult.Exit -> {}
                    }
                }
                entryPlay { playScreenResult ->
                    Log.d("NavigationRoot", "playScreenResult: $playScreenResult")
                    when (playScreenResult) {
                        PlayScreenResult.Exit -> {
                            rootBackStack.removeLastOrNull()
                        }
                    }
                }
            },
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
    )
}
