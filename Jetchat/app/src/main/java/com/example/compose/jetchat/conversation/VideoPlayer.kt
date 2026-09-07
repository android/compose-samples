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

import android.graphics.Bitmap
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberMuteButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPresentationState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.example.compose.jetchat.R
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

const val DEFAULT_VIDEO_URL =
    "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_5MB.mp4"

/**
 * Lightweight video thumbnail displayed inside scrollable lists (LazyColumn).
 * Shows an extracted video frame or styled gradient placeholder with a centered play icon.
 * Clicking triggers [onClick] to navigate to the full-screen video player screen.
 */
@Composable
fun VideoThumbnail(videoUri: String, onClick: () -> Unit, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(16.dp)) {
    val context = LocalContext.current
    val resolvedUri = remember(videoUri) { resolveVideoUri(videoUri) }

    val thumbnailBitmap by produceState<Bitmap?>(initialValue = null, resolvedUri) {
        withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                val uriString = resolvedUri.toString()
                if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                    retriever.setDataSource(uriString, HashMap<String, String>())
                } else {
                    retriever.setDataSource(context, resolvedUri)
                }
                val bitmap = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    ?: retriever.frameAtTime
                retriever.release()
                value = bitmap
            } catch (e: Throwable) {
                // Fallback to stylized dark gradient placeholder on network/codec error
            }
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF262638),
                        Color(0xFF14141E),
                    ),
                ),
            )
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.play_video),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailBitmap != null) {
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = stringResource(R.string.play_video),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
            )
        }

        // Circular frosted play button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = CircleShape,
                )
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.6f),
                    shape = CircleShape,
                ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play_arrow),
                contentDescription = stringResource(R.string.play_video),
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

/**
 * Fullscreen video player screen where the SurfaceView extends across the whole surface of the screen,
 * and controls are free to extend across the whole screen.
 */
@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(videoUri: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resolvedUri = remember(videoUri) { resolveVideoUri(videoUri) }

    val exoPlayer = remember(context, resolvedUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(resolvedUri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
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

    BackHandler(onBack = onDismiss)

    // Hide system status and navigation bars for immersive edge-to-edge playback
    ImmersiveSystemBarsEffect()

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

                val left = boxLeft.coerceIn(0f, surfaceW)
                val top = boxTop.coerceIn(0f, surfaceH)
                val right =  boxRight.coerceIn(0f, surfaceW)
                val bottom = boxBottom.coerceIn(0f, surfaceH)

                if (right > left && bottom > top) {
                    val cornerRadius =  spec.cornerRadiusPx
                    spec.copy(
                        boundsInSurface = RectF(left, top, right, bottom),
                        cornerRadiusPx = cornerRadius,
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
        SurfaceViewBlurHelper.applyBlurRegions(sv, resolvedRegions)
    }

    DisposableEffect(Unit) {
        onDispose {
            surfaceViewRef?.let { sv ->
                exoPlayer.clearVideoSurface()
                SurfaceViewBlurHelper.clearBlurRegions(sv)
            }
            surfaceViewRef = null
        }
    }

    LaunchedEffect(controlsVisible) {
        updateBlurRegions()
    }

    // Auto-hide controls after playback inactivity
    val isPlaying = !playPauseButtonState.showPlay
    LaunchedEffect(controlsVisible, isPlaying, userInteractedTime) {
        if (controlsVisible && isPlaying) {
            delay(5000.milliseconds)
            controlsVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
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
        // SurfaceView sized to fit video aspect ratio using Media3's resizeWithContentScale
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
                .resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)
                .onGloballyPositioned { coords ->
                    surfaceCoordinates = coords
                    updateBlurRegions()
                },
            update = { sv ->
                surfaceViewRef = sv
                updateBlurRegions()
            },
            onRelease = { sv ->
                exoPlayer.clearVideoSurfaceView(sv)
                SurfaceViewBlurHelper.clearBlurRegions(sv)
                surfaceViewRef = null
            },
        )

        // Shutter while video is loading
        if (presentationState.coverSurface) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            )
        }

        // Overlay controls allowed to extend to the whole surface of the screen
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
                isFullscreen = true,
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
                onToggleFullscreen = onDismiss,
                onUpdateBlurRegions = { updateBlurRegions() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Drop-in backward-compatible composable for displaying video.
 * Displays a thumbnail in place and launches the fullscreen player screen when tapped.
 */
@Composable
fun VideoPlayer(videoUri: String, modifier: Modifier = Modifier, autoPlay: Boolean = false, shape: Shape = RoundedCornerShape(16.dp)) {
    var isPlayerOpen by rememberSaveable { mutableStateOf(autoPlay) }

    if (isPlayerOpen) {
        FullScreenVideoPlayer(
            videoUri = videoUri,
            onDismiss = { isPlayerOpen = false },
        )
    } else {
        VideoThumbnail(
            videoUri = videoUri,
            onClick = { isPlayerOpen = true },
            shape = shape,
            modifier = modifier,
        )
    }
}

private fun resolveVideoUri(videoUri: String): Uri {
    return if (videoUri.isNotBlank()) {
        videoUri.toUri()
    } else {
        DEFAULT_VIDEO_URL.toUri()
    }
}
