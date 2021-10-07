package com.example.jetnews.ui.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent

/**
 * Note: this method intercepts the key event rather than passing it on to children
 */
fun Modifier.interceptKey(key: Key, onKeyEvent: ()-> Unit): Modifier {
    return this.onPreviewKeyEvent {
        @OptIn(ExperimentalComposeUiApi::class)
        if(it.key == key) {
            onKeyEvent()
            true
        }
        else {
            false
        }
    }
}