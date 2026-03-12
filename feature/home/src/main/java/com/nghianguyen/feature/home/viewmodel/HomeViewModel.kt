package com.nghianguyen.feature.home.viewmodel

import android.util.Log
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuGameRepository

/**
 * ViewModel for the Home screen.
 * Responsible for checking if a game is in progress.
 *
 * @property sudokuGameRepository Repository for accessing Sudoku game data.
 */
class HomeViewModel(
    private val sudokuGameRepository: SudokuGameRepository
) : BaseViewModel<HomeScreenState, HomeAction, HomeEvent>() {

    private var gameInProgressId: Long? = null

    override fun buildInitialState() = HomeScreenState(gameInProgress = false)

    override fun onStart() {
        launch {
            sudokuGameRepository.getGameInProgress()
                .onSuccess {
                    gameInProgressId = it.firstOrNull()?.id
                    Log.d("HomeViewModel", "gameInProgressId: $gameInProgressId")
                    updateState { copy(gameInProgress = gameInProgressId != null) }
                }
                .onFailure {

                }
        }
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnScanClick -> {
                launch {
                    sendEvent(HomeEvent.NavigateToScanner)
                }
            }
            is HomeAction.OnContinueClick -> {
                launch {
                    gameInProgressId?.let {
                        sendEvent(HomeEvent.NavigateToPlay(it))
                    }
                }
            }
        }
    }
}
