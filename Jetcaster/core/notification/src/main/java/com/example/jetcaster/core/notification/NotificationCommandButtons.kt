/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.core.notification

import android.os.Bundle
import androidx.media3.session.SessionCommand

private const val ACTION_SEEK_FORWARD = "action_seek_forward"
private const val ACTION_SEEK_REWIND = "action_seek_rewind"
private const val ACTION_PLAY_AND_PAUSE = "action_play_and_pause"
private const val ACTION_PREVIOUS = "action_previous"
private const val ACTION_NEXT = "action_next"

enum class NotificationCommandButtons(
    val customAction: String,
    val displayName: String,
    val iconResId: (Boolean) -> Int,
    val sessionCommand: SessionCommand,
) {
    PREVIOUS(
        customAction = ACTION_PREVIOUS,
        displayName = "Previous",
        iconResId = { androidx.media3.session.R.drawable.media3_notification_seek_to_previous },
        sessionCommand = SessionCommand(ACTION_PREVIOUS, Bundle.EMPTY)
    ),
    SEEK_REWIND(
        customAction = ACTION_SEEK_REWIND,
        displayName = "SeekRewind",
        iconResId = { androidx.media3.session.R.drawable.media3_notification_seek_back },
        sessionCommand = SessionCommand(ACTION_SEEK_REWIND, Bundle.EMPTY)
    ),
    PLAY_AND_PAUSE(
        customAction = ACTION_PLAY_AND_PAUSE,
        displayName = "PlayPause",
        iconResId = { isPlaying ->
            if (isPlaying) {
                androidx.media3.session.R.drawable.media3_notification_pause
            } else {
                androidx.media3.session.R.drawable.media3_notification_play
            }
        },
        sessionCommand = SessionCommand(ACTION_PLAY_AND_PAUSE, Bundle.EMPTY)
    ),
    SEEK_FORWARD(
        customAction = ACTION_SEEK_FORWARD,
        displayName = "SeekForward",
        iconResId = { androidx.media3.session.R.drawable.media3_notification_seek_forward },
        sessionCommand = SessionCommand(ACTION_SEEK_FORWARD, Bundle.EMPTY)
    ),
    NEXT(
        customAction = ACTION_NEXT,
        displayName = "Next",
        iconResId = { androidx.media3.session.R.drawable.media3_notification_seek_to_next },
        sessionCommand = SessionCommand(ACTION_NEXT, Bundle.EMPTY)
    );
}
