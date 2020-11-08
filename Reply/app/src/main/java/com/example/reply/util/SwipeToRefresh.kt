package com.example.reply

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import com.example.reply.ui.EmailContainerPadding
import com.example.reply.ui.SwipeViewContainerHeight
import com.example.reply.ui.SwipeViewContainerPadding

private val RefreshDistance = 4.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val refreshDistance = with(DensityAmbient.current) { RefreshDistance.toPx() }
    val state = rememberSwipeableState(refreshingState) { newValue ->
        // compare both copies of the swipe state before calling onRefresh(). This is a workaround.
        if (newValue && !refreshingState) onRefresh()
        true
    }

    Box(
        modifier = Modifier.swipeable(
            state = state,
            anchors = mapOf(
                -refreshDistance to false,
                refreshDistance to true
            ),
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Vertical
        )
    ) {

        if (state.offset.value > 0 ) {
            content(Modifier.padding(
                top = SwipeViewContainerHeight + SwipeViewContainerPadding + EmailContainerPadding
            ))
            refreshIndicator()
        } else {
            content(Modifier)
        }


        // TODO (https://issuetracker.google.com/issues/164113834): This state->event trampoline is a
        //  workaround for a bug in the SwipableState API. Currently, state.value is a duplicated
        //  source of truth of refreshingState.
        onCommit(refreshingState) {
            state.animateTo(refreshingState)
        }
    }
}
