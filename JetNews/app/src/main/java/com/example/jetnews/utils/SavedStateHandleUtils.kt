package com.example.jetnews.utils

import android.os.Bundle
import androidx.compose.MutableState
import androidx.compose.mutableStateOf
import androidx.lifecycle.SavedStateHandle

/**
 * Return a [MutableState] that will automatically be saved in a [SavedStateHandle].
 *
 * This can be used from ViewModels to create a compose-observable value that survives rotation. It
 * supports arbitrary types with manual conversion to a [Bundle].
 *
 * @param save convert [T] to a [Bundle] for saving
 * @param restore restore a [T] from a [Bundle]
 */
fun <T> SavedStateHandle.getMutableStateOf(
    key: String,
    save: (T) -> Bundle,
    restore: (Bundle?) -> T
): MutableState<T> {
    val initial = restore(get(key))
    val state = mutableStateOf(initial)
    setSavedStateProvider(key) {
        save(state.value)
    }
    return state
}
