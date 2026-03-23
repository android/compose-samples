
@file:OptIn(ExperimentalMediaQueryApi::class, ExperimentalComposeUiApi::class)
package com.example.jetsnack.ui.utils

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.example.jetsnack.ui.theme.JetsnackTheme

class DesktopPreviewWrapper : PreviewWrapper {
    private val themeWrapper = ThemeWrapper()
    private val lookaheadScopeWrapper = LookaheadScopeWrapper()

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        // Nest the wrappers: Theme is usually the outermost layer,
        // followed by the environment/container wrapper.
        themeWrapper.Wrap {
            lookaheadScopeWrapper.Wrap {
                content()
            }
        }
    }
}

@Composable
fun UiMediaScopeWrapper(
    keyboardKind: UiMediaScope.KeyboardKind,
    pointerPrecision: UiMediaScope.PointerPrecision,
    content: @Composable () -> Unit) {

    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    BoxWithConstraints {
        val uiMediaScope = object : UiMediaScope {
            override val keyboardKind: UiMediaScope.KeyboardKind
                get() = keyboardKind
            override val windowPosture: UiMediaScope.Posture
                get() = UiMediaScope.Posture.Flat
            override val windowWidth = maxWidth // faking the width and height for previews
            override val windowHeight = maxHeight
            override val pointerPrecision = pointerPrecision
            override val hasMicrophone = true
            override val hasCamera = true
            override val viewingDistance = UiMediaScope.ViewingDistance.Near
        }

        JetsnackTheme {
            CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope) {
                content()
            }
        }

    }
}


// Assuming KeyboardKind is your enum/class
@OptIn(ExperimentalMediaQueryApi::class)
class KeyboardKindProvider : PreviewParameterProvider<UiMediaScope.KeyboardKind> {
    override val values: Sequence<UiMediaScope.KeyboardKind> = sequenceOf(
        UiMediaScope.KeyboardKind.Physical,
        UiMediaScope.KeyboardKind.Virtual,
    )
}
@OptIn(ExperimentalMediaQueryApi::class)
class PointerPrecisionProvider : PreviewParameterProvider<UiMediaScope.PointerPrecision> {
    override val values: Sequence<UiMediaScope.PointerPrecision> = sequenceOf(
        UiMediaScope.PointerPrecision.Fine,
        UiMediaScope.PointerPrecision.Blunt,
        UiMediaScope.PointerPrecision.Coarse,
        UiMediaScope.PointerPrecision.None
    )
}