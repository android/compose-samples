package com.example.jetnews.ui

import androidx.animation.TweenBuilder
import androidx.compose.*
import androidx.compose.frames.modelMapOf
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Opacity

@Composable
fun <T> Crossfade(current: T, child: @Composable() (T) -> Unit) {
    val state = +memo { CrossfadeState<T>() }
    if (current != state.current) {
        state.current = current
        state.items.remove(current)
        state.items.keys.forEach {
            state.items[it] = @Composable() {
                Fade(false, true) {
                    child(it)
                }
            }
        }
        state.items[current] = @Composable() {
            Fade(true, true, {
                if (current == state.current) {
                    state.items.clear()
                    state.items[current] = @Composable() {
                        Fade(true, false) {
                            child(current)
                        }
                    }
                }
            }) {
                child(current)
            }
        }
    }
    state.items.forEach { entry ->
        Key(entry.key) {
            entry.value()
        }
    }
}

private class CrossfadeState<T> {
    var current: T? = null
    val items = modelMapOf<T, @Composable() () -> Unit>()
}

@Composable
private fun Fade(
    visible: Boolean,
    withAnimation: Boolean,
    onAnimationFinish: () -> Unit = {},
    child: @Composable() () -> Unit
) {
    val opacity = if (withAnimation) {
        val animatedFloat = +animatedFloat(if (!visible) 1f else 0f)
        +onCommit(visible) {
            animatedFloat.animateTo(
                if (visible) 1f else 0f,
                anim = TweenBuilder<Float>().apply { duration = 250 },
                onEnd = { _, _ -> onAnimationFinish() })
        }
        animatedFloat.value
    } else {
        if (visible) 1f else 0f
    }
    Opacity(opacity) {
        child()
    }
}
