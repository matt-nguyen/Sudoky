package com.nghianguyen.data.sudoku.local.ext

import android.database.SQLException
import android.util.Log
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.annotation.UnsafeResultErrorAccess
import com.github.michaelbull.result.mapError
import com.nghianguyen.domain.model.LocalDataError
import kotlinx.coroutines.CancellationException

@OptIn(UnsafeResultErrorAccess::class)
fun <V> Result<V, Throwable>.mapLocalDataError(): Result<V, LocalDataError> {
    return mapError {
        Log.e("mapLocalDataError", "Exception captured during operation", error)
        if (error is CancellationException)
            throw error

        when (error) {
            is SQLException -> LocalDataError.DATABASE_ERROR
            else -> throw error
        }
    }
}
