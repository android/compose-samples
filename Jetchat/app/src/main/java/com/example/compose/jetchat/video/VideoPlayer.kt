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

package com.example.compose.jetchat.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn as AndroidxOptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
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
import com.example.compose.jetchat.conversation.BlurRegionSpec
import com.example.compose.jetchat.conversation.SurfaceViewBlurHelper
import kotlin.coroutines.resume
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

const val DEFAULT_VIDEO_URL = "asset:///delightful_tips.mp4"

// Provided alongside NavDisplay(sharedTransitionScope = ...); can be dropped once Navigation 3
// exposes a CompositionLocal for its SharedTransitionScope.
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

fun videoSharedElementKey(messageId: String?, videoUri: String): String = "video-${messageId ?: videoUri}"

/**
 * Shared bounds modifier helper that configures shared transition bounds and overlay clipping
 * for video elements transitioning between conversation bubbles and the full-screen player.
 */
@Composable
private fun Modifier.videoSharedBounds(
    sharedContentState: SharedTransitionScope.SharedContentState,
    shape: Shape,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalAnimatedVisibilityScope.current,
): Modifier {
    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@videoSharedBounds.sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransition = OverlayClip(shape),
            )
        }
    } else {
        this
    }
}

/**
 * Animates the video container corners between sharp fullscreen corners (0.dp) and the chat
 * bubble's rounded corners so both ends of the shared element transition match at every frame.
 */
@Composable
private fun rememberAnimatedVideoShape(
    isUserMe: Boolean,
    isFullscreenDestination: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalAnimatedVisibilityScope.current,
    fallbackShape: Shape = RoundedCornerShape(16.dp),
): Shape {
    if (animatedVisibilityScope == null) return fallbackShape
    val fractionState = animatedVisibilityScope.transition.animateFloat(
        label = "videoBubbleCornerFraction",
    ) { state ->
        if ((state == EnterExitState.Visible) == isFullscreenDestination) 0f else 1f
    }
    return remember(isUserMe, fractionState) {
        object : Shape {
            override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline = with(density) {
                val fraction = fractionState.value
                val major = lerp(0.dp, 20.dp, fraction).toPx()
                val minor = lerp(0.dp, 4.dp, fraction).toPx()
                val topStart = if (isUserMe) major else minor
                val topEnd = if (isUserMe) minor else major
                val isLtr = layoutDirection == LayoutDirection.Ltr
                Outline.Rounded(
                    RoundRect(
                        rect = size.toRect(),
                        topLeft = CornerRadius(if (isLtr) topStart else topEnd),
                        topRight = CornerRadius(if (isLtr) topEnd else topStart),
                        bottomRight = CornerRadius(major),
                        bottomLeft = CornerRadius(major),
                    ),
                )
            }
        }
    }
}

private const val MAX_INITIAL_THUMBNAILS = 20

/**
 * Reactive in-memory frame cache shared between [VideoThumbnail] and [FullScreenVideoPlayer]
 * so the shrinking player surface and target chat card seamlessly display the same video frame
 * during transitions, then revert to the original video thumbnail once the transition finishes.
 */
private object VideoFrameCache {
    private val initialThumbnails = mutableStateMapOf<String, Bitmap>()
    private val initialThumbnailKeys = ArrayDeque<String>()
    private val playbackSnapshots = mutableStateMapOf<String, Bitmap>()
    private val activePlayerSnapshotReady = mutableStateMapOf<String, Boolean>()

    operator fun get(cacheKey: String, uriKey: String): Bitmap? = playbackSnapshots[cacheKey] ?: initialThumbnails[uriKey]

    operator fun set(cacheKey: String, bitmap: Bitmap) {
        playbackSnapshots[cacheKey] = bitmap
        activePlayerSnapshotReady[cacheKey] = true
    }

    fun isSnapshotReady(cacheKey: String): Boolean = activePlayerSnapshotReady[cacheKey] ?: true

    /**
     * True while a [FullScreenVideoPlayer] for [cacheKey] is alive (it registers a snapshot
     * ready/stale entry after its first frame and clears it on dispose). Unlike the shared
     * element match state, this is already true in the first composition of the conversation
     * during a predictive back gesture.
     */
    fun hasActivePlayer(cacheKey: String): Boolean = activePlayerSnapshotReady.containsKey(cacheKey)

    fun markSnapshotStale(cacheKey: String) {
        activePlayerSnapshotReady[cacheKey] = false
    }

    fun markSnapshotReady(cacheKey: String) {
        activePlayerSnapshotReady[cacheKey] = true
    }

    fun getInitialThumbnail(uriKey: String): Bitmap? = initialThumbnails[uriKey]

    fun setInitialThumbnail(uriKey: String, bitmap: Bitmap) {
        if (initialThumbnails[uriKey] == null) {
            if (initialThumbnailKeys.size >= MAX_INITIAL_THUMBNAILS) {
                val oldestKey = initialThumbnailKeys.removeFirstOrNull()
                if (oldestKey != null) {
                    initialThumbnails.remove(oldestKey)
                }
            }
            initialThumbnailKeys.addLast(uriKey)
            initialThumbnails[uriKey] = bitmap
        }
    }

    fun clearPlaybackSnapshot(cacheKey: String) {
        playbackSnapshots.remove(cacheKey)
        activePlayerSnapshotReady.remove(cacheKey)
    }
}

/**
 * Remembers and loads a cached bitmap from [VideoFrameCache] or extracts the initial frame
 * asynchronously using [extractInitialVideoFrame].
 */
@Composable
private fun rememberCachedVideoBitmap(resolvedUri: Uri, cacheKey: String): Bitmap? {
    val context = LocalContext.current
    val uriKey = resolvedUri.toString()
    val bitmap = VideoFrameCache[cacheKey, uriKey]
    LaunchedEffect(resolvedUri) {
        if (VideoFrameCache.getInitialThumbnail(uriKey) == null) {
            extractInitialVideoFrame(context, resolvedUri)?.let { bmp ->
                VideoFrameCache.setInitialThumbnail(uriKey, bmp)
            }
        }
    }
    return bitmap
}

private const val MAX_SNAPSHOT_DIMENSION_PX = 720f

private fun downscaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
    val maxSrc = maxOf(bitmap.width, bitmap.height)
    if (maxSrc <= MAX_SNAPSHOT_DIMENSION_PX) return bitmap
    val scale = MAX_SNAPSHOT_DIMENSION_PX / maxSrc
    val dstW = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
    val dstH = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
    return bitmap.scale(dstW, dstH)
}

private fun Bitmap.toSizeOrNull(): Size? = if (width > 0 && height > 0) Size(width.toFloat(), height.toFloat()) else null

private suspend fun extractInitialVideoFrame(context: Context, resolvedUri: Uri): Bitmap? = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    try {
        val uriString = resolvedUri.toString()
        if (uriString.startsWith("asset:///") || uriString.startsWith("file:///android_asset/")) {
            val assetPath = uriString
                .removePrefix("asset:///")
                .removePrefix("file:///android_asset/")
            context.assets.openFd(assetPath).use { afd ->
                retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
        } else {
            retriever.setDataSource(context, resolvedUri)
        }
        val frame = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            ?: retriever.frameAtTime
        frame?.let(::downscaleBitmapIfNeeded)
    } catch (_: Throwable) {
        null
    } finally {
        retriever.release()
    }
}

private suspend fun captureSurfaceFrame(surfaceView: SurfaceView): Bitmap? {
    if (!surfaceView.isAttachedToWindow || !surfaceView.holder.surface.isValid) return null
    val srcW = surfaceView.width
    val srcH = surfaceView.height
    if (srcW <= 0 || srcH <= 0) return null

    val scale = (MAX_SNAPSHOT_DIMENSION_PX / maxOf(srcW, srcH)).coerceAtMost(1f)
    val dstW = (srcW * scale).roundToInt().coerceAtLeast(1)
    val dstH = (srcH * scale).roundToInt().coerceAtLeast(1)

    val bitmap = createBitmap(dstW, dstH, Bitmap.Config.ARGB_8888)
    return withTimeoutOrNull(45.milliseconds) {
        suspendCancellableCoroutine { cont ->
            try {
                PixelCopy.request(
                    surfaceView,
                    bitmap,
                    { copyResult ->
                        if (cont.isActive) {
                            cont.resume(if (copyResult == PixelCopy.SUCCESS) bitmap else null)
                        }
                    },
                    Handler(Looper.getMainLooper()),
                )
            } catch (_: Throwable) {
                if (cont.isActive) {
                    cont.resume(null)
                }
            }
        }
    }
}

/**
 * Sizes a video container to fit [sourceSize] within incoming constraints during normal/lookahead
 * layout, while honoring exact fixed constraints from [SharedTransitionScope.ResizeMode.RemeasureToBounds]
 * during shared element approach measurement.
 */
private fun Modifier.fitVideoBounds(sourceSize: Size?): Modifier = layout { measurable, constraints ->
    val maxW = constraints.maxWidth
    val maxH = constraints.maxHeight
    var targetW = maxW
    var targetH = maxH
    if (
        !(constraints.hasFixedWidth && constraints.hasFixedHeight) &&
        sourceSize != null && sourceSize.width > 0f && sourceSize.height > 0f &&
        maxW > 0 && maxH > 0
    ) {
        val scale = ContentScale.Fit.computeScaleFactor(
            sourceSize,
            Size(maxW.toFloat(), maxH.toFloat()),
        )
        targetW = (sourceSize.width * scale.scaleX).roundToInt().coerceIn(constraints.minWidth, maxW)
        targetH = (sourceSize.height * scale.scaleY).roundToInt().coerceIn(constraints.minHeight, maxH)
    }
    val placeable = measurable.measure(Constraints.fixed(targetW, targetH))
    layout(targetW, targetH) {
        placeable.place(0, 0)
    }
}

/**
 * Holds [player] via [retain] so playback state and the decoded surface survive configuration
 * changes and shared-element pop transitions without releasing and re-preparing the player.
 */
private class RetainedExoPlayer(val player: ExoPlayer) : RetainObserver {
    override fun onRetired() = player.release()
    override fun onUnused() = player.release()
    override fun onRetained() {}
    override fun onEnteredComposition() {}
    override fun onExitedComposition() {}
}

/**
 * Lightweight video thumbnail displayed inside scrollable lists (LazyColumn).
 * Shows an extracted video frame or styled gradient placeholder with a centered play icon.
 * Clicking triggers [onClick] to navigate to the full-screen video player screen.
 */
@Composable
fun VideoThumbnail(
    videoUri: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    sharedElementKey: String? = null,
    isUserMe: Boolean = true,
) {
    val resolvedUri = remember(videoUri) { resolveVideoUri(videoUri) }
    val cacheKey = sharedElementKey ?: resolvedUri.toString()
    val thumbnailBitmap = rememberCachedVideoBitmap(resolvedUri, cacheKey)
    val thumbnailImageBitmap = remember(thumbnailBitmap) { thumbnailBitmap?.asImageBitmap() }
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val sharedContentState = if (sharedElementKey != null && sharedTransitionScope != null) {
        with(sharedTransitionScope) { rememberSharedContentState(key = sharedElementKey) }
    } else {
        null
    }
    val effectiveShape = if (sharedContentState != null) {
        rememberAnimatedVideoShape(
            isUserMe = isUserMe,
            isFullscreenDestination = false,
            fallbackShape = shape,
        )
    } else {
        shape
    }
    // A fullscreen player is alive for this video (predictive back in progress, enter transition,
    // or a cancelled gesture). The shared element match is only established after the first
    // frame the conversation is composed, so this is the signal that is correct on frame one.
    val hasActivePlayer = sharedElementKey != null && VideoFrameCache.hasActivePlayer(cacheKey)
    val isThisElementTransitioning =
        sharedTransitionScope?.isTransitionActive == true && sharedContentState?.isMatchFound == true
    val isHandingOffToPlayer = isThisElementTransitioning || hasActivePlayer
    var isVisibleInViewport by remember { mutableStateOf(hasActivePlayer) }
    val sharedBoundsModifier = if (sharedContentState != null && (isVisibleInViewport || isHandingOffToPlayer)) {
        Modifier.videoSharedBounds(sharedContentState = sharedContentState, shape = effectiveShape)
    } else {
        Modifier
    }
    val hideUntilSnapshotReady =
        isHandingOffToPlayer && !VideoFrameCache.isSnapshotReady(cacheKey)
    // Hide the play overlay the instant the player hands off to this thumbnail so it never flashes
    // over the transitioning video frame; only animate it back in once the player is gone.
    // The gate is applied directly (not via the animated value) so there is no one-frame delay.
    val animatedPlayOverlayAlpha = animateFloatAsState(
        targetValue = if (isHandingOffToPlayer) 0f else 1f,
        label = "playOverlayAlpha",
    )

    Box(
        modifier = modifier
            .onVisibilityChanged(minFractionVisible = 0.001f) { isVisible ->
                isVisibleInViewport = isVisible
            }
            .then(sharedBoundsModifier)
            .clip(effectiveShape)
            .graphicsLayer {
                alpha = if (hideUntilSnapshotReady) 0f else 1f
            }
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
        if (thumbnailImageBitmap != null) {
            Image(
                bitmap = thumbnailImageBitmap,
                contentDescription = stringResource(R.string.play_video),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Dim scrim and circular frosted play button, faded together while a player is active
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = if (isHandingOffToPlayer) 0f else animatedPlayOverlayAlpha.value
                },
        ) {
            if (thumbnailImageBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                )
            }
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
}

/**
 * Fullscreen video player screen where the SurfaceView extends across the whole surface of the screen,
 * and controls are free to extend across the whole screen.
 */
@AndroidxOptIn(UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(
    videoUri: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    messageText: String? = null,
    messageId: String? = null,
    isUserMe: Boolean = true,
    enableBackHandler: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val resolvedUri = remember(videoUri) { resolveVideoUri(videoUri) }
    val cacheKey = videoSharedElementKey(messageId, videoUri)

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    // Walk up to the root SeekableTransitionState-backed Transition from NavDisplay:
    // when a predictive back gesture is cancelled and snapTo() is called, the child
    // EnterExitState transition's updateTarget(Visible) shifts its previous targetState
    // (PostExit) into currentState while the root transition is already settled.
    val rootTransition = remember(animatedVisibilityScope) {
        var root: Transition<*>? = animatedVisibilityScope?.transition
        while (root?.parentTransition != null) {
            root = root.parentTransition
        }
        root
    }

    var isDismissing by remember { mutableStateOf(false) }
    val inTransition = isDismissing ||
        (sharedTransitionScope?.isTransitionActive == true) ||
        (rootTransition != null && rootTransition.currentState != rootTransition.targetState)

    val cachedBitmap = rememberCachedVideoBitmap(resolvedUri, cacheKey)
    val cachedImageBitmap = remember(cachedBitmap) { cachedBitmap?.asImageBitmap() }

    val applicationContext = context.applicationContext
    val exoPlayer = retain(resolvedUri) {
        RetainedExoPlayer(
            ExoPlayer.Builder(applicationContext).build().apply {
                setMediaItem(MediaItem.fromUri(resolvedUri))
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
                prepare()
            },
        )
    }.player

    var hasRenderedFrameOnSurface by remember { mutableStateOf(false) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                hasRenderedFrameOnSurface = true
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    val activity = LocalActivity.current
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        if (activity?.isChangingConfigurations != true) {
            exoPlayer.pause()
        }
    }

    val playPauseButtonState = rememberPlayPauseButtonState(exoPlayer)
    val presentationState = rememberPresentationState(exoPlayer)
    val muteButtonState = rememberMuteButtonState(exoPlayer)
    val progressState = rememberProgressStateWithTickInterval(exoPlayer, tickIntervalMs = 200L)

    var controlsVisible by remember { mutableStateOf(true) }
    var userInteractedTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var surfaceViewRef by remember { mutableStateOf<SurfaceView?>(null) }
    val surfaceBoundsRef = remember { Ref<IntRect>() }
    val blurRegionSpecs = remember { mutableMapOf<String, BlurRegionSpec>() }

    suspend fun snapshotCurrentFrame() {
        val sv = surfaceViewRef?.takeIf { !presentationState.coverSurface && hasRenderedFrameOnSurface }
        val bmp = sv?.let { captureSurfaceFrame(it) }
        if (bmp != null) {
            VideoFrameCache[cacheKey] = bmp
        } else {
            VideoFrameCache.markSnapshotReady(cacheKey)
        }
    }

    val handleDismiss: () -> Unit = {
        if (!isDismissing) {
            isDismissing = true
            exoPlayer.pause()
            scope.launch {
                snapshotCurrentFrame()
                onDismiss()
            }
        }
    }

    if (enableBackHandler) {
        BackHandler(onBack = handleDismiss)
    }

    // Hide system status and navigation bars for immersive edge-to-edge playback
    ImmersiveSystemBarsEffect()

    fun updateBlurRegions() {
        val sv = surfaceViewRef ?: return
        if (!controlsVisible || inTransition) {
            SurfaceViewBlurHelper.clearBlurRegions(sv)
            return
        }
        val surfaceBounds = surfaceBoundsRef.value ?: return
        val surfaceW = surfaceBounds.width.toFloat()
        val surfaceH = surfaceBounds.height.toFloat()
        if (surfaceW <= 0f || surfaceH <= 0f) return

        val resolvedRegions = blurRegionSpecs.values.mapNotNull { spec ->
            val box = spec.boundsInWindow
            if (box.width > 0 && box.height > 0) {
                val boxLeft = box.left.toFloat() - surfaceBounds.left.toFloat()
                val boxTop = box.top.toFloat() - surfaceBounds.top.toFloat()
                val boxRight = box.right.toFloat() - surfaceBounds.left.toFloat()
                val boxBottom = box.bottom.toFloat() - surfaceBounds.top.toFloat()

                val left = boxLeft.coerceIn(0f, surfaceW)
                val top = boxTop.coerceIn(0f, surfaceH)
                val right = boxRight.coerceIn(0f, surfaceW)
                val bottom = boxBottom.coerceIn(0f, surfaceH)

                if (right > left && bottom > top) {
                    val r = spec.cornerRadiusPx.coerceAtLeast(0f)
                    val clipLeft = boxLeft <= 0f
                    val clipTop = boxTop <= 0f
                    val clipRight = boxRight >= surfaceW
                    val clipBottom = boxBottom >= surfaceH

                    val topLeft = if (clipLeft || clipTop) 0f else r
                    val topRight = if (clipRight || clipTop) 0f else r
                    val bottomLeft = if (clipLeft || clipBottom) 0f else r
                    val bottomRight = if (clipRight || clipBottom) 0f else r

                    val allSame = topLeft == topRight && topRight == bottomLeft && bottomLeft == bottomRight
                    spec.copy(
                        boundsInSurface = RectF(left, top, right, bottom),
                        cornerRadiusPx = if (allSame) topLeft else 0f,
                        cornerRadiiPx = if (allSame) {
                            null
                        } else {
                            listOf(
                                topLeft, topLeft,
                                topRight, topRight,
                                bottomLeft, bottomLeft,
                                bottomRight, bottomRight,
                            )
                        },
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

    // The SurfaceView itself is detached from the player in AndroidView's onRelease.
    DisposableEffect(cacheKey) {
        onDispose {
            if (activity?.isChangingConfigurations != true) {
                VideoFrameCache.clearPlaybackSnapshot(cacheKey)
            }
        }
    }

    // Pause playback as soon as a predictive back gesture (DeferredTransition) or exit transition begins,
    // and resume playback if the predictive back gesture is cancelled.
    var shouldResumeAfterGesture by remember { mutableStateOf(false) }
    val isEnterComplete = animatedVisibilityScope?.transition?.currentState == EnterExitState.Visible
    val isExitingOrPredictiveBack = isDismissing ||
        animatedVisibilityScope?.transition?.targetState == EnterExitState.PostExit ||
        (isEnterComplete && inTransition)

    val isSnapshotReady = VideoFrameCache.isSnapshotReady(cacheKey)
    val waitingForExitSnapshot =
        isExitingOrPredictiveBack && hasRenderedFrameOnSurface && !isSnapshotReady
    val showSnapshotOverlay =
        (inTransition && !waitingForExitSnapshot) ||
            presentationState.coverSurface ||
            !hasRenderedFrameOnSurface

    LaunchedEffect(isExitingOrPredictiveBack) {
        if (isExitingOrPredictiveBack) {
            if (exoPlayer.playWhenReady) {
                shouldResumeAfterGesture = true
                exoPlayer.pause()
            }
            // Re-read rather than capturing isSnapshotReady: the composition-time value may be
            // stale by the time this effect runs.
            if (!isDismissing && !VideoFrameCache.isSnapshotReady(cacheKey)) {
                snapshotCurrentFrame()
            }
        } else if (shouldResumeAfterGesture && !isDismissing) {
            shouldResumeAfterGesture = false
            VideoFrameCache.markSnapshotStale(cacheKey)
            exoPlayer.play()
        }
    }

    val isPlaying = !playPauseButtonState.showPlay
    LaunchedEffect(isPlaying, hasRenderedFrameOnSurface, presentationState.coverSurface, inTransition) {
        if (inTransition || presentationState.coverSurface || !hasRenderedFrameOnSurface) return@LaunchedEffect
        if (isPlaying) {
            VideoFrameCache.markSnapshotStale(cacheKey)
        } else {
            snapshotCurrentFrame()
        }
    }

    // Auto-hide controls after playback inactivity
    LaunchedEffect(controlsVisible, isPlaying, userInteractedTime) {
        if (controlsVisible && isPlaying) {
            delay(5000.milliseconds)
            controlsVisible = false
        }
    }

    val effectiveVideoSize = presentationState.videoSizeDp ?: cachedBitmap?.toSizeOrNull()

    val animatedVideoShape = rememberAnimatedVideoShape(
        isUserMe = isUserMe,
        isFullscreenDestination = true,
        fallbackShape = RoundedCornerShape(0.dp),
    )

    val sharedSurfaceModifier = sharedTransitionScope?.let { transitionScope ->
        Modifier.videoSharedBounds(
            sharedContentState = with(transitionScope) { rememberSharedContentState(key = cacheKey) },
            shape = animatedVideoShape,
        )
    } ?: Modifier

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
        // Keep SurfaceView outside sharedBounds so SharedTransitionLayout never resizes or detaches
        // the underlying hardware surface during transitions or cancelled predictive back gestures.
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    surfaceViewRef = this
                    alpha = if (showSnapshotOverlay) 0f else 1f
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
                                hasRenderedFrameOnSurface = false
                                SurfaceViewBlurHelper.clearBlurRegions(this@apply)
                            }
                        },
                    )
                }
            },
            modifier = Modifier
                .resizeWithContentScale(ContentScale.Fit, effectiveVideoSize)
                .onLayoutRectChanged(throttleMillis = 0, debounceMillis = 0) { bounds ->
                    surfaceBoundsRef.value = bounds.boundsInWindow
                    updateBlurRegions()
                },
            update = { sv ->
                sv.alpha = if (showSnapshotOverlay) 0f else 1f
                updateBlurRegions()
            },
            onRelease = { sv ->
                exoPlayer.clearVideoSurfaceView(sv)
                SurfaceViewBlurHelper.clearBlurRegions(sv)
                surfaceViewRef = null
            },
        )

        // Shared element container that smoothly morphs the captured video frame between the
        // fullscreen video bounds and the chat bubble card during enter/exit/predictive back.
        // Always retain children and control visibility via graphicsLayer alpha so Lookahead and
        // idle fullscreen bounds always match the exact fitted video dimensions (never 0x0).
        Box(
            modifier = Modifier
                .then(sharedSurfaceModifier)
                .fitVideoBounds(effectiveVideoSize)
                .clip(animatedVideoShape)
                .graphicsLayer {
                    alpha = if (showSnapshotOverlay) 1f else 0f
                },
            contentAlignment = Alignment.Center,
        ) {
            if (cachedImageBitmap != null) {
                Image(
                    bitmap = cachedImageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                )
            }
        }

        // Overlay controls allowed to extend to the whole surface of the screen
        AnimatedVisibility(
            visible = controlsVisible && !inTransition,
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
                        VideoFrameCache.markSnapshotStale(cacheKey)
                        exoPlayer.seekTo((progressFraction * duration).toLong())
                    }
                },
                onToggleFullscreen = handleDismiss,
                onUpdateBlurRegions = { updateBlurRegions() },
                messageText = messageText,
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
fun VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
    messageText: String? = null,
) {
    var isPlayerOpen by rememberSaveable { mutableStateOf(autoPlay) }

    if (isPlayerOpen) {
        FullScreenVideoPlayer(
            videoUri = videoUri,
            onDismiss = { isPlayerOpen = false },
            messageText = messageText,
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
