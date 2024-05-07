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

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val SEEK_TO_DURATION = 10_000L

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MediaPlayerService : MediaSessionService() {

    @Inject
    lateinit var notifier: Notifier

    @Inject
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification =
                notifier.createMediaNotification(actionFactory)

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean = false
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val player = mediaSession.player
        if (!player.playWhenReady ||
            player.mediaItemCount == 0 ||
            player.playbackState == Player.STATE_ENDED
        ) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession = mediaSession
}
