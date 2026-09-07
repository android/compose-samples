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

import android.graphics.RectF
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.state.rememberMuteButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPresentationState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.example.compose.jetchat.R
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

const val DEFAULT_VIDEO_URL =
    "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_5MB.mp4"

/**
 * Composable video player utilizing SurfaceView and the platform setBlurRegions API
 * to blur regions directly underneath the overlay controls.
 *
 * Hoists a single ExoPlayer instance, eliminating redundant boolean flags and state
 * duplication between inline and fullscreen playback.
 */
@Composable
fun VideoPlayer(videoUri: String, modifier: Modifier = Modifier, autoPlay: Boolean = false, shape: Shape = RoundedCornerShape(16.dp)) {
    val context = LocalContext.current
    val resolvedUri = remember(videoUri) { resolveVideoUri(videoUri) }

    // Single ExoPlayer instance shared seamlessly between inline and fullscreen views
    val exoPlayer = remember(context, resolvedUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(resolvedUri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = autoPlay
            prepare()
        }
    }
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        exoPlayer.pause()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        exoPlayer.pause()
    }

    var isFullscreen by rememberSaveable { mutableStateOf(false) }

    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
            ),
        ) {
            ImmersiveDialogEffect()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                VideoPlayerSurface(
                    exoPlayer = exoPlayer,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(0.dp),
                    isFullscreen = true,
                    onToggleFullscreen = { isFullscreen = false },
                )
            }
        }

        Box(
            modifier = modifier
                .clip(shape)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.fullscreen_video),
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    } else {
        VideoPlayerSurface(
            exoPlayer = exoPlayer,
            modifier = modifier,
            shape = shape,
            isFullscreen = false,
            onToggleFullscreen = { isFullscreen = true },
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerSurface(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
) {
    // Read state from Media3 UI Compose state holders
    val playPauseButtonState = rememberPlayPauseButtonState(exoPlayer)
    val presentationState = rememberPresentationState(exoPlayer)
    val muteButtonState = rememberMuteButtonState(exoPlayer)
    val progressState = rememberProgressStateWithTickInterval(exoPlayer, tickIntervalMs = 200L)

    var controlsVisible by remember { mutableStateOf(true) }
    var userInteractedTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var surfaceViewRef by remember { mutableStateOf<SurfaceView?>(null) }
    var surfaceCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val blurRegionSpecs = remember { mutableMapOf<String, BlurRegionSpec>() }

    fun updateBlurRegions() {
        val sv = surfaceViewRef ?: return
        if (!controlsVisible) {
            SurfaceViewBlurHelper.clearBlurRegions(sv)
            return
        }
        val coords = surfaceCoordinates
        if (coords == null || !coords.isAttached) {
            return
        }
        val surfacePos = coords.positionInWindow()
        val surfaceW = coords.size.width.toFloat()
        val surfaceH = coords.size.height.toFloat()
        if (surfaceW <= 0f || surfaceH <= 0f) return

        val resolvedRegions = blurRegionSpecs.values.mapNotNull { spec ->
            val box = spec.boundsInWindow
            if (box.width > 0 && box.height > 0) {
                val boxLeft = box.left.toFloat() - surfacePos.x
                val boxTop = box.top.toFloat() - surfacePos.y
                val boxRight = box.right.toFloat() - surfacePos.x
                val boxBottom = box.bottom.toFloat() - surfacePos.y

                val extendsPastBase = boxBottom >= surfaceH - 4f
                val extendsAcrossWidth = (boxLeft <= 24f || boxLeft <= surfaceW * 0.1f) &&
                    (boxRight >= surfaceW - 24f || boxRight >= surfaceW * 0.9f)

                val isFullRectangle = extendsPastBase || extendsAcrossWidth

                val left = if (extendsAcrossWidth) 0f else boxLeft.coerceIn(0f, surfaceW)
                val top = boxTop.coerceIn(0f, surfaceH)
                val right = if (extendsAcrossWidth) surfaceW else boxRight.coerceIn(0f, surfaceW)
                val bottom = if (extendsPastBase) surfaceH else boxBottom.coerceIn(0f, surfaceH)

                if (right > left && bottom > top) {
                    // The region overlaps outside the SurfaceView, so the corner radius
                    // shouldn't be applied in this case
                    val cornerRadius = if (isFullRectangle) 0f else spec.cornerRadiusPx
                    spec.copy(
                        boundsInSurface = RectF(left, top, right, bottom),
                        cornerRadiusPx = cornerRadius,
                    )
                } else {
                    null
                }
            } else if (spec.boundsInSurface.width() > 0f && spec.boundsInSurface.height() > 0f) {
                spec
            } else {
                null
            }
        }
        SurfaceViewBlurHelper.applyBlurRegions(sv, resolvedRegions)
    }

    LaunchedEffect(controlsVisible) {
        updateBlurRegions()
    }

    // Auto-hide controls after a period of playback inactivity
    val isPlaying = !playPauseButtonState.showPlay
    LaunchedEffect(controlsVisible, isPlaying, userInteractedTime) {
        if (controlsVisible && isPlaying) {
            delay(5000.milliseconds)
            controlsVisible = false
        }
    }

    val videoAspectRatio = remember(presentationState.videoSizeDp) {
        val size = presentationState.videoSizeDp
        if (size != null && size.width > 0f && size.height > 0f) {
            size.width / size.height
        } else {
            16f / 9f
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                userInteractedTime = System.currentTimeMillis()
                controlsVisible = !controlsVisible
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.aspectRatio(videoAspectRatio, matchHeightConstraintsFirst = true),
            contentAlignment = Alignment.Center,
        ) {
            // SurfaceView rendering the video directly from ExoPlayer
            AndroidView(
                factory = { ctx ->
                    SurfaceView(ctx).apply {
                        surfaceViewRef = this
                        exoPlayer.setVideoSurfaceView(this)
                        holder.addCallback(
                            object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    updateBlurRegions()
                                }

                                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                                    updateBlurRegions()
                                }

                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    SurfaceViewBlurHelper.clearBlurRegions(this@apply)
                                }
                            },
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        surfaceCoordinates = coords
                        updateBlurRegions()
                    },
                update = { sv ->
                    surfaceViewRef = sv
                    updateBlurRegions()
                },
            )

            // Placeholder shutter while video is loading / resetting
            if (presentationState.coverSurface) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                )
            }
        }

        // Overlay controls that blur the regions underneath using SurfaceView#setBlurRegions
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            VideoPlayerOverlayControls(
                playPauseButtonState = playPauseButtonState,
                muteButtonState = muteButtonState,
                progressState = progressState,
                isFullscreen = isFullscreen,
                surfaceCoordinates = surfaceCoordinates,
                blurRegionSpecs = blurRegionSpecs,
                onUserInteraction = {
                    userInteractedTime = System.currentTimeMillis()
                },
                onSeekTo = { progressFraction ->
                    val duration = progressState.durationMs
                    if (duration > 0) {
                        exoPlayer.seekTo((progressFraction * duration).toLong())
                    }
                },
                onToggleFullscreen = onToggleFullscreen,
                onUpdateBlurRegions = { updateBlurRegions() },
            )
        }
    }
}

private fun resolveVideoUri(videoUri: String): Uri {
    return if (videoUri.isNotBlank()) {
        videoUri.toUri()
    } else {
        DEFAULT_VIDEO_URL.toUri()
    }
}
