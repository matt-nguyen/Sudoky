package com.nghianguyen.feature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A base class for all ViewModels in the application. It manages UI state, actions, and one-time
 * events.
 *
 * @param STATE The type representing the UI state.
 * @param ACTION The type representing user actions or intents.
 * @param EVENT The type representing one-time UI events.
 */
abstract class BaseViewModel<STATE, ACTION, EVENT>() : ViewModel() {

    /**
     * Builds and returns the initial state for the UI. This is called when the ViewModel is
     * created.
     */
    protected abstract fun buildInitialState(): STATE

    /**
     * Called when the [uiState] flows are first collected. Use this to trigger any initial data
     * loading or start observing other flows.
     */
    protected abstract suspend fun onStart()

    /**
     * Handles an incoming user [ACTION].
     *
     * @param action The action to be processed.
     */
    abstract fun handleAction(action: ACTION)

    private val _uiState = MutableStateFlow(buildInitialState())

    /** A [kotlinx.coroutines.flow.StateFlow] representing the current UI state. */
    val uiState =
        _uiState
            .onStart { onStart() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _uiState.value)

    private val _uiEvent = Channel<EVENT>(Channel.BUFFERED)

    /** A [kotlinx.coroutines.flow.SharedFlow] for one-time events that the UI should react to. */
    val uiEvent =
        _uiEvent.receiveAsFlow().shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))

    /**
     * Updates the current UI state using the provided [updater] function.
     *
     * @param updater Lambda function to update the current state.
     */
    protected fun updateState(updater: STATE.() -> STATE) {
        _uiState.update(updater)
    }

    /**
     * Dispatches a one-time [EVENT] to the UI.
     *
     * @param event The event to be sent.
     */
    protected fun sendEvent(event: EVENT) {
        launch { _uiEvent.send(event) }
    }

    /**
     * Helper function to launch a coroutine in the [viewModelScope] with a default exception
     * handler.
     *
     * @param coroutineContext Optional context to add to the coroutine.
     * @param block The suspend block to execute.
     */
    protected fun launch(
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(
            context =
                if (coroutineContext[CoroutineExceptionHandler] != null) coroutineContext
                else coroutineContext + defaultCoroutineExceptionHandler,
            block = block,
        )
    }

    private val defaultCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ViewModel", "Unexpected error", throwable)
    }
}
