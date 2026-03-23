
@file:OptIn(ExperimentalMediaQueryApi::class)
package com.example.jetsnack.ui.utils



import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.tooling.preview.PreviewWrapper


class DesktopPreviewWrapper : PreviewWrapper {
    private val themeWrapper = ThemeWrapper()
    private val desktopPreviewWrapper = DesktopUiMediaScopeWrapper()

    private val lookaheadScopeWrapper = LookaheadScopeWrapper()

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        // Nest the wrappers: Theme is usually the outermost layer,
        // followed by the environment/container wrapper.
        themeWrapper.Wrap {
            lookaheadScopeWrapper.Wrap {
                desktopPreviewWrapper.Wrap {
                    content()
                }
            }
        }
    }
}

/**
 * This class is a workaround for UiMediaQuery not exposing previews just yet.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
class DesktopUiMediaScopeWrapper: PreviewWrapper {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        // Step 1: Enable the mediaQuery function
        ComposeUiFlags.isMediaQueryIntegrationEnabled = true
        BoxWithConstraints {
            // Step 2: Define a custom object implementing the UiMediaScope interface.
            val uiMediaScope = object : UiMediaScope {
                override val keyboardKind: UiMediaScope.KeyboardKind
                    get() = UiMediaScope.KeyboardKind.Physical
                override val windowPosture: UiMediaScope.Posture
                    get() = UiMediaScope.Posture.Flat
                override val windowWidth = maxWidth // faking the width and height for previews
                override val windowHeight = maxHeight
                override val pointerPrecision = UiMediaScope.PointerPrecision.Fine
                override val hasMicrophone = true
                override val hasCamera = true
                override val viewingDistance = UiMediaScope.ViewingDistance.Near
            }

            // Step 3: Set the object to the LocalUiMediaScope.
            CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope) {
                content()
            }
        }
    }
}
@OptIn(ExperimentalComposeUiApi::class)
class PhoneUiMediaScopeWrapper: PreviewWrapper {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        // Step 1: Enable the mediaQuery function
        ComposeUiFlags.isMediaQueryIntegrationEnabled = true
        BoxWithConstraints {
            // Step 2: Define a custom object implementing the UiMediaScope interface.
            val uiMediaScope = object : UiMediaScope {
                override val keyboardKind: UiMediaScope.KeyboardKind
                    get() = UiMediaScope.KeyboardKind.Virtual
                override val windowPosture: UiMediaScope.Posture
                    get() = UiMediaScope.Posture.Flat
                override val windowWidth = maxWidth // faking the width and height for previews
                override val windowHeight = maxHeight
                override val pointerPrecision = UiMediaScope.PointerPrecision.Blunt
                override val hasMicrophone = true
                override val hasCamera = true
                override val viewingDistance = UiMediaScope.ViewingDistance.Near
            }

            // Step 3: Set the object to the LocalUiMediaScope.
            CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope) {
                content()
            }
        }
    }
}
