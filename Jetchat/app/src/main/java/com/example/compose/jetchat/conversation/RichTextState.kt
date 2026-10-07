/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.conversation

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.ExpandPolicy
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach

/**
 * Toggleable inline text styles.
 *
 * Each [spanStyle] sets only 1 attribute so that:
 * 1. Toggling an attribute only adds, trims, or removes ranges of that constant without touching
 *    independent attributes on overlapping ranges.
 * 2. The same immutable [SpanStyle] instance is reused across all ranges without extra allocations.
 */
enum class InlineStyle(val spanStyle: SpanStyle) {
    Bold(SpanStyle(fontWeight = FontWeight.Bold)),
    Italic(SpanStyle(fontStyle = FontStyle.Italic)),
    Underline(SpanStyle(textDecoration = TextDecoration.Underline)),
    Strikethrough(SpanStyle(textDecoration = TextDecoration.LineThrough)),
    LargeFont(SpanStyle(fontSize = 28.sp)),
    ;

    internal val mask: Int = 1 shl ordinal
}

private val InlineStyleBySpanStyle: Map<SpanStyle, InlineStyle> =
    InlineStyle.entries.associateBy { it.spanStyle }

internal val RedColorSpanStyle = SpanStyle(color = Color(0xFFD32F2F))
internal val BlueColorSpanStyle = SpanStyle(color = Color(0xFF1976D2))
internal val GreenColorSpanStyle = SpanStyle(color = Color(0xFF388E3C))
internal val PurpleColorSpanStyle = SpanStyle(color = Color(0xFF8E24AA))
internal val ColorSpanStyles =
    listOf(RedColorSpanStyle, BlueColorSpanStyle, GreenColorSpanStyle, PurpleColorSpanStyle)

/**
 * Creates and remembers a [FormattedTextState].
 *
 * The text, selection, and inline [SpanStyle] ranges survive configuration changes and process
 * death via [FormattedTextState.Saver]. [initialText] is only used when the state is first created.
 * Pending collapsed-selection toggles are transient and not saved.
 */
@Composable
fun rememberFormattedTextState(initialText: String = ""): FormattedTextState = rememberSaveable(saver = FormattedTextState.Saver) {
    FormattedTextState(initialText)
}

/**
 * A rich-text [BasicTextField] wrapper that accepts a [FormattedTextState].
 */
@Composable
fun FormattedTextField(
    state: FormattedTextState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    cursorBrush: Brush = SolidColor(Color.Black),
) {
    BasicTextField(
        state = state.textFieldState,
        modifier = modifier,
        inputTransformation = state.inputTransformation,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        cursorBrush = cursorBrush,
    )
}

/**
 * Wraps [TextFieldState] and [SpanStyleState] for rich inline text formatting.
 */
@Stable
class FormattedTextState internal constructor(val textFieldState: TextFieldState) {
    constructor(initialText: String = "") : this(TextFieldState(initialText))

    val spanStyleState: SpanStyleState = SpanStyleState(textFieldState)

    internal val inputTransformation: InputTransformation = InputTransformation {
        with(spanStyleState) { applyPendingOverrides() }
    }

    val text: CharSequence
        get() = textFieldState.text

    /**
     * Whether [text] is blank. Backed by `derivedStateOf`, so readers are only invalidated when the
     * value flips, not on every keystroke.
     */
    val isBlank: Boolean by derivedStateOf { textFieldState.text.isBlank() }

    /**
     * Whether [text] is empty. Backed by `derivedStateOf`, so readers are only invalidated when the
     * value flips, not on every keystroke.
     */
    val isEmpty: Boolean by derivedStateOf { textFieldState.text.isEmpty() }

    /**
     * Clears all text, inline [SpanStyle] ranges, and pending style overrides.
     */
    fun clear() {
        spanStyleState.clearOverrides()
        textFieldState.edit {
            getSpanStyles(TextRange(0, length)).fastForEach { removeStyle(it) }
            replace(0, length, "")
        }
    }

    /**
     * Inserts [newString] at the current selection and places the cursor after the inserted text.
     */
    fun addText(newString: String) {
        textFieldState.edit {
            replace(selection.min, selection.max, newString)
            with(spanStyleState) { applyPendingOverrides() }
        }
    }

    /**
     * Builds an [AnnotatedString] containing the current text and all active [SpanStyle] ranges.
     */
    fun toAnnotatedString(): AnnotatedString {
        val currentText = textFieldState.text.toString()
        val spanStyles = textFieldState.textStyles.getSpanStyles(TextRange(0, currentText.length))
        if (spanStyles.isEmpty()) return AnnotatedString(currentText)
        return buildAnnotatedString {
            append(currentText)
            spanStyles.fastForEach { range ->
                addStyle(range.item, range.start, range.end)
            }
        }
    }

    companion object {
        /**
         * Saves the underlying [TextFieldState] (text, selection, undo history and inline styles)
         * by delegating to [TextFieldState.Saver].
         */
        val Saver: Saver<FormattedTextState, Any> = Saver(
            save = { state -> with(TextFieldState.Saver) { save(state.textFieldState) } },
            restore = { saved ->
                TextFieldState.Saver.restore(saved)?.let { FormattedTextState(it) }
            },
        )
    }
}

/**
 * Resolved inline-style state for the current selection: a bitmask of active [InlineStyle]s plus
 * the active color. Cheap structural equality keeps `derivedStateOf` readers from recomposing
 * unless something actually changed.
 */
@Immutable
private data class ResolvedStyles(val activeMask: Int = 0, val activeColorStyle: SpanStyle? = null) {
    fun isActive(style: InlineStyle): Boolean = activeMask and style.mask != 0

    fun withStyle(style: InlineStyle, active: Boolean): ResolvedStyles = copy(
        activeMask = if (active) activeMask or style.mask else activeMask and
            style.mask.inv(),
    )

    companion object {
        val None = ResolvedStyles()
    }
}

/**
 * Manages inline [InlineStyle]s and the active color for a [TextFieldState] in a single
 * `derivedStateOf` pass, and handles collapsed-selection style overrides for the next insert edit.
 */
@Stable
class SpanStyleState(private val state: TextFieldState) {

    private val computed by derivedStateOf { computeStyles(state.selection) }

    /**
     * Computes which styles fully cover the current selection.
     *
     * Coverage is the *union* of matching ranges (clipped to the query range), so overlapping
     * ranges of the same style are never double-counted. A color is only active when a single
     * color covers the whole range.
     */
    private fun computeStyles(selection: TextRange): ResolvedStyles {
        // When selection is collapsed, newly inserted text with ExpandPolicy.AtEnd inherits the
        // style of the previous character [selection.start - 1, selection.start).
        val queryRange =
            if (!selection.collapsed) {
                TextRange(selection.min, selection.max)
            } else if (selection.start > 0) {
                TextRange(selection.start - 1, selection.start)
            } else {
                return ResolvedStyles.None
            }

        val spanStyles = state.textStyles.getSpanStyles(queryRange)
        if (spanStyles.isEmpty()) return ResolvedStyles.None

        val queryMin = queryRange.min
        val queryMax = queryRange.max
        val requiredCoverage = queryMax - queryMin

        // Per-style union coverage. Processing ranges in start order lets us compute the union
        // incrementally: each range only contributes the part beyond what was already reached.
        val inlineCovered = IntArray(InlineStyle.entries.size)
        val inlineReach = IntArray(InlineStyle.entries.size) { queryMin }
        val colorCovered = IntArray(ColorSpanStyles.size)
        val colorReach = IntArray(ColorSpanStyles.size) { queryMin }

        val sorted = if (spanStyles.size > 1) spanStyles.sortedBy { it.start } else spanStyles
        sorted.fastForEach { range ->
            val start = maxOf(range.start, queryMin)
            val end = minOf(range.end, queryMax)
            if (start >= end) return@fastForEach

            val inline = InlineStyleBySpanStyle[range.item]
            if (inline != null) {
                val i = inline.ordinal
                inlineCovered[i] += (end - maxOf(start, inlineReach[i])).coerceAtLeast(0)
                inlineReach[i] = maxOf(inlineReach[i], end)
                return@fastForEach
            }
            val c = ColorSpanStyles.indexOf(range.item)
            if (c >= 0) {
                colorCovered[c] += (end - maxOf(start, colorReach[c])).coerceAtLeast(0)
                colorReach[c] = maxOf(colorReach[c], end)
            }
        }

        var mask = 0
        InlineStyle.entries.fastForEach { style ->
            if (inlineCovered[style.ordinal] >= requiredCoverage) mask = mask or style.mask
        }
        val colorIndex = colorCovered.indexOfFirst { it >= requiredCoverage }
        return ResolvedStyles(mask, ColorSpanStyles.getOrNull(colorIndex))
    }

    private var overrideSelection by mutableStateOf<TextRange?>(null)
    private val inlineOverrides = mutableStateMapOf<InlineStyle, Boolean>()
    private var hasColorOverride by mutableStateOf(false)
    private var colorOverride by mutableStateOf<SpanStyle?>(null)

    private val hasValidOverride: Boolean
        get() = state.selection.collapsed && overrideSelection == state.selection

    private fun prepareCollapsedOverride() {
        if (overrideSelection != state.selection) {
            clearOverrides()
            overrideSelection = state.selection
        }
    }

    internal fun clearOverrides() {
        overrideSelection = null
        inlineOverrides.clear()
        hasColorOverride = false
        colorOverride = null
    }

    /**
     * Pending overrides merged with [computed], resolved in a single `derivedStateOf`.
     *
     * Readers (e.g. toolbar buttons) are only invalidated when a resolved value actually changes,
     * rather than on every selection/text change that [hasValidOverride] would otherwise observe.
     */
    private val resolved by derivedStateOf {
        val base = computed
        if (!hasValidOverride) return@derivedStateOf base
        var result = base
        inlineOverrides.forEach { (style, active) -> result = result.withStyle(style, active) }
        if (hasColorOverride) result = result.copy(activeColorStyle = colorOverride)
        result
    }

    /** Whether [style] is active for the current selection (including pending toggles). */
    fun isActive(style: InlineStyle): Boolean = resolved.isActive(style)

    val activeColorStyle: SpanStyle?
        get() = resolved.activeColorStyle

    val isColorActive: Boolean
        get() = activeColorStyle != null

    /**
     * Toggles [style]. With a collapsed selection the toggle is held as a pending override and
     * applied to the next inserted text; otherwise it is applied to the selected range.
     */
    fun toggle(style: InlineStyle) {
        if (state.selection.collapsed) {
            val next = !isActive(style)
            prepareCollapsedOverride()
            inlineOverrides[style] = next
        } else {
            val currentlyActive = computed.isActive(style)
            state.edit {
                if (currentlyActive) {
                    removeSpanStyle(selection, style.spanStyle)
                } else {
                    applySpanStyle(selection, style.spanStyle)
                }
            }
        }
    }

    fun toggleColor(colorStyle: SpanStyle) {
        val isSelected = activeColorStyle == colorStyle
        if (state.selection.collapsed) {
            prepareCollapsedOverride()
            hasColorOverride = true
            colorOverride = if (isSelected) null else colorStyle
        } else {
            state.edit {
                ColorSpanStyles.fastForEach { removeSpanStyle(selection, it) }
                if (!isSelected) {
                    applySpanStyle(selection, colorStyle)
                }
            }
        }
    }

    /**
     * Cycles through [ColorSpanStyles] in order, and turns color off (`null`) when the last color
     * in [ColorSpanStyles] is currently active.
     */
    fun cycleColor() {
        val current = activeColorStyle
        val currentIndex = if (current != null) ColorSpanStyles.indexOf(current) else -1
        val targetStyle =
            if (currentIndex == -1) {
                ColorSpanStyles.first()
            } else if (currentIndex < ColorSpanStyles.lastIndex) {
                ColorSpanStyles[currentIndex + 1]
            } else {
                current!!
            }
        toggleColor(targetStyle)
    }

    internal fun TextFieldBuffer.applyPendingOverrides() {
        if (changes.changeCount == 0) return
        // Only apply the style state to the first change; ignore any following edits' style
        // implications.
        val originalRange = changes.getOriginalRange(0)
        val range = changes.getRange(0)

        // Only override when the first change is insert-only (originalRange.collapsed &&
        // !range.collapsed) at the cursor where the button was toggled. For delete-only or
        // replace (delete then insert), do nothing and let selection change recompute the
        // state.
        if (originalRange.collapsed && !range.collapsed && overrideSelection == originalRange) {
            val base = computed
            inlineOverrides.forEach { (style, shouldBeActive) ->
                if (shouldBeActive != base.isActive(style)) {
                    if (shouldBeActive) {
                        applySpanStyle(range, style.spanStyle)
                    } else {
                        removeSpanStyle(range, style.spanStyle)
                    }
                }
            }
            if (hasColorOverride && colorOverride != base.activeColorStyle) {
                ColorSpanStyles.fastForEach { removeSpanStyle(range, it) }
                colorOverride?.let { applySpanStyle(range, it) }
            }
        }
        clearOverrides()
    }
}

private fun TextFieldBuffer.removeSpanStyle(targetRange: TextRange, spanStyle: SpanStyle) {
    val min = targetRange.min
    val max = targetRange.max
    val intersectingStyles = getSpanStyles(targetRange).fastFilter { it.spanStyle == spanStyle }
    intersectingStyles.fastForEach { style ->
        val range = style.textRange
        if (range.start >= min && range.end <= max) {
            removeStyle(style)
        } else if (range.start < min && range.end > max) {
            val oldEnd = range.end
            style.textRange = TextRange(range.start, min)
            // Reuse the same constant SpanStyle instance for the split trailing range with
            // ExpandPolicy.AtEnd.
            addStyle(spanStyle, TextRange(max, oldEnd), ExpandPolicy.AtEnd)
        } else if (range.start < min) {
            style.textRange = TextRange(range.start, min)
        } else {
            style.textRange = TextRange(max, range.end)
        }
    }
}

private fun TextFieldBuffer.applySpanStyle(targetRange: TextRange, spanStyle: SpanStyle) {
    // getSpanStyles excludes ranges that merely touch the query, so widen it by 1 on each side to
    // also coalesce adjacent ranges of the same style. A range intersects the widened query iff it
    // overlaps or touches the target. This keeps the span list from fragmenting (e.g. bolding "ab"
    // then "cd" yields a single [a, d) range rather than two).
    val queryRange = TextRange(
        (targetRange.min - 1).coerceAtLeast(0),
        (targetRange.max + 1).coerceAtMost(length),
    )
    val mergeCandidates = getSpanStyles(queryRange).fastFilter { it.spanStyle == spanStyle }
    var mergedStart = targetRange.min
    var mergedEnd = targetRange.max
    mergeCandidates.fastForEach { style ->
        mergedStart = minOf(mergedStart, style.textRange.start)
        mergedEnd = maxOf(mergedEnd, style.textRange.end)
        removeStyle(style)
    }
    // Reuse the same constant SpanStyle instance on the coalesced range.
    addStyle(spanStyle, TextRange(mergedStart, mergedEnd), ExpandPolicy.AtEnd)
}
