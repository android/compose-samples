package com.example.jetsnack.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.example.jetsnack.ui.theme.JetsnackTheme

class ThemeWrapper: PreviewWrapper {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        JetsnackTheme {
            content()
        }
    }
}
class LookaheadScopeWrapper: PreviewWrapper {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        LookaheadScope {
            content()
        }
    }

}