/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.R
import kotlin.math.abs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordButton(
    recording: Boolean,
    swipeOffset: () -> Float,
    onSwipeOffsetChange: (Float) -> Unit,
    onStartRecording: () -> Boolean,
    onFinishRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = recording, label = "record")
    val scale = transition.animateFloat(
        transitionSpec = { spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) 2f else 1f }
    )
    val containerAlpha = transition.animateFloat(
        transitionSpec = { tween(2000) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) 1f else 0f }
    )
    val iconColor = transition.animateColor(
        transitionSpec = { tween(200) },
        label = "record-scale",
        targetValueByState = { rec ->
            if (rec) contentColorFor(LocalContentColor.current)
            else LocalContentColor.current
        }
    )

    Box {
        // Background during recording
        Box(
            Modifier
                .matchParentSize()
                .aspectRatio(1f)
                .graphicsLayer {
                    alpha = containerAlpha.value
                    scaleX = scale.value; scaleY = scale.value
                }
                .clip(CircleShape)
                .background(LocalContentColor.current)
        )
        val scope = rememberCoroutineScope()
        val tooltipState = remember { TooltipState() }
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = {
                RichTooltip {
                    Text(stringResource(R.string.touch_and_hold_to_record))
                }
            },
            enableUserInput = false,
            state = tooltipState
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = stringResource(R.string.record_message),
                tint = iconColor.value,
                modifier = modifier
                    .sizeIn(minWidth = 56.dp, minHeight = 6.dp)
                    .padding(18.dp)
                    .clickable { }
                    .voiceRecordingGesture(
                        horizontalSwipeProgress = swipeOffset,
                        onSwipeProgressChanged = onSwipeOffsetChange,
                        onClick = { scope.launch { tooltipState.show() } },
                        onStartRecording = onStartRecording,
                        onFinishRecording = onFinishRecording,
                        onCancelRecording = onCancelRecording,
                    )
            )
        }
    }
}

private fun Modifier.voiceRecordingGesture(
    horizontalSwipeProgress: () -> Float,
    onSwipeProgressChanged: (Float) -> Unit,
    onClick: () -> Unit = {},
    onStartRecording: () -> Boolean = { false },
    onFinishRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    swipeToCancelThreshold: Dp = 200.dp,
    verticalThreshold: Dp = 80.dp,
): Modifier = this
    .pointerInput(Unit) { detectTapGestures { onClick() } }
    .pointerInput(Unit) {
        var offsetY = 0f
        var dragging = false
        val swipeToCancelThresholdPx = swipeToCancelThreshold.toPx()
        val verticalThresholdPx = verticalThreshold.toPx()

        detectDragGesturesAfterLongPress(
            onDragStart = {
                onSwipeProgressChanged(0f)
                offsetY = 0f
                dragging = true
                onStartRecording()
            },
            onDragCancel = {
                onCancelRecording()
                dragging = false
            },
            onDragEnd = {
                if (dragging) {
                    onFinishRecording()
                }
                dragging = false
            },
            onDrag = { change, dragAmount ->
                if (dragging) {
                    onSwipeProgressChanged(horizontalSwipeProgress() + dragAmount.x)
                    offsetY += dragAmount.y
                    val offsetX = horizontalSwipeProgress()
                    if (
                        offsetX < 0 &&
                        abs(offsetX) >= swipeToCancelThresholdPx &&
                        abs(offsetY) <= verticalThresholdPx
                    ) {
                        onCancelRecording()
                        dragging = false
                    }
                }
            }
        )
    }
