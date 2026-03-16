package com.example.jetsnack.ui.components

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import com.example.jetsnack.ui.theme.JetsnackTheme

// Workaround for b/492528450 - setting textStyle currently doesn't set fontFamily.
@ExperimentalFoundationStyleApi
fun StyleScope.jetsnackTextStyle(value: TextStyle) {
    textStyle(value)
    value.fontFamily?.let { fontFamily(it) }
}

@ExperimentalFoundationStyleApi
@Composable
fun JetsnackText(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    ) {
    BasicText(
        text = text,
        modifier = modifier.styleable(null, JetsnackTheme.appStyles.defaultTextStyle, style),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
    )
}
