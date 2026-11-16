package com.nghianguyen.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nghianguyen.feature.viewmodel.BaseViewModel
import com.nghianguyen.sudoku.SudokuGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Home screen. Responsible for checking if a game is in progress.
 *
 * @property sudokuGameRepository Repository for accessing Sudoku game data.
 */
class HomeViewModel(private val sudokuGameRepository: SudokuGameRepository) :
    BaseViewModel<HomeAction, HomeEvent>() {

    private var gameInProgressId: Long? = null

    private val _uiState = MutableStateFlow(HomeScreenState(gameInProgress = false))
    private val gameInProgress = flow {
        sudokuGameRepository.getGameInProgress()
            .onSuccess { games ->
                gameInProgressId = games.firstOrNull()?.id
                Log.d("HomeViewModel", "gameInProgressId: $gameInProgressId")
                emit(gameInProgressId != null)
            }
            .onFailure {}
    }
    val uiState =
        combine(_uiState, gameInProgress) { state, hasGame ->
            state.copy(gameInProgress = hasGame)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _uiState.value)

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnScanClick -> {
                launch { sendEvent(HomeEvent.NavigateToScanner) }
            }
            is HomeAction.OnContinueClick -> {
                launch { gameInProgressId?.let { sendEvent(HomeEvent.NavigateToPlay(it)) } }
            }
        }
    }
}
