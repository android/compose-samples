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
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@OptIn(UnstableApi::class)
internal class CustomMediaSessionCallback : MediaSession.Callback {
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult =
        if (session.isMediaNotificationController(controller)) {
            val sessionCommandBuilder =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()

            val playerCommand = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                .removeAll(
                    Player.COMMAND_SEEK_TO_PREVIOUS,
                    Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM,
                    Player.COMMAND_SEEK_TO_NEXT,
                    Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                )

            NotificationCommandButtons.entries.forEach { commandButton ->
                commandButton.sessionCommand.apply(sessionCommandBuilder::add)
            }

            MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommandBuilder.build())
                .setAvailablePlayerCommands(playerCommand.build())
                .setCustomLayout(
                    NotificationCommandButtons.entries.map {
                        CommandButton.Builder()
                            .setDisplayName(it.displayName)
                            .setIconResId(it.iconResId(session.player.isPlaying))
                            .setSessionCommand(it.sessionCommand)
                            .build()
                    }
                )
                .build()
        } else {
            MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {

        when (customCommand.customAction) {
            NotificationCommandButtons.PREVIOUS.customAction -> {
                session.player.run {
                    if (hasPreviousMediaItem()) seekToPreviousMediaItem()
                    else seekToPrevious()
                }
            }

            NotificationCommandButtons.SEEK_REWIND.customAction -> {
                session.player.run {
                    seekTo(currentPosition - SEEK_TO_DURATION)
                }
            }

            NotificationCommandButtons.PLAY_AND_PAUSE.customAction -> {
                if (!session.player.isPlaying) session.player.play()
                else session.player.pause()
            }

            NotificationCommandButtons.SEEK_FORWARD.customAction -> {
                session.player.run {
                    seekTo(currentPosition + SEEK_TO_DURATION)
                }
            }

            NotificationCommandButtons.NEXT.customAction -> {
                session.player.run {
                    if (hasNextMediaItem()) seekToNextMediaItem()
                }
            }

            else -> {
                // TODO()
            }
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}
