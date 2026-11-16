package com.nghianguyen.feature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A base class for all ViewModels in the application. It manages actions and one-time
 * events.
 *
 * @param ACTION The type representing user actions or intents.
 * @param EVENT The type representing one-time UI events.
 */
abstract class BaseViewModel<ACTION, EVENT> : ViewModel() {

    /**
     * Handles an incoming user [ACTION].
     *
     * @param action The action to be processed.
     */
    abstract fun handleAction(action: ACTION)

    private val _uiEvent = Channel<EVENT>(Channel.BUFFERED)

    /** A [kotlinx.coroutines.flow.SharedFlow] for one-time events that the UI should react to. */
    val uiEvent =
        _uiEvent.receiveAsFlow().shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))

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
