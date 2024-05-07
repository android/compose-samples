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

package com.example.jetcaster.core.notification.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.example.jetcaster.core.notification.ConnectedMediaController
import com.example.jetcaster.core.notification.CustomMediaSessionCallback
import com.example.jetcaster.core.notification.MediaControllerManager
import com.example.jetcaster.core.notification.MediaNotificationManager
import com.example.jetcaster.core.notification.MediaPlayerService
import com.example.jetcaster.core.notification.Notifier
import com.google.common.util.concurrent.ListenableFuture
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaServiceBinds {

    @Binds
    abstract fun bindsNotifier(
        mediaNotification: MediaNotificationManager
    ): Notifier

    @Binds
    abstract fun bindsMediaControllerManager(
        connectedMediaController: ConnectedMediaController
    ): MediaControllerManager
}

@Module
@InstallIn(SingletonComponent::class)
object MediaServiceModule {

    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()

    @Singleton
    @Provides
    fun providesMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession =
        MediaSession.Builder(context, player)
            .setCallback(CustomMediaSessionCallback())
            .build()

    @Singleton
    @Provides
    fun providesMediaNotificationManager(
        @ApplicationContext context: Context,
        mediaSession: MediaSession,
    ): MediaNotificationManager =
        MediaNotificationManager(context, mediaSession)

    @Singleton
    @Provides
    fun providesSessionToken(
        @ApplicationContext context: Context
    ): SessionToken =
        SessionToken(context, ComponentName(context, MediaPlayerService::class.java))

    @Singleton
    @Provides
    fun providesListenableFutureMediaController(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> =
        MediaController
            .Builder(context, sessionToken)
            .buildAsync()
}
