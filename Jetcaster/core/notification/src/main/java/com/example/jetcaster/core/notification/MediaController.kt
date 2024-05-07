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

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface MediaControllerManager {
    val mediaControllerFlow: Flow<MediaController>
}

@Singleton
class ConnectedMediaController @Inject constructor(
    mediaControllerFuture: ListenableFuture<MediaController>,
) : MediaControllerManager {

    override val mediaControllerFlow: Flow<MediaController> = callbackFlow {
        Futures.addCallback(
            mediaControllerFuture,
            object : FutureCallback<MediaController> {
                override fun onSuccess(result: MediaController) {
                    trySend(result)
                }

                override fun onFailure(t: Throwable) {
                    cancel(CancellationException(t.message))
                }
            },
            MoreExecutors.directExecutor()
        )

        awaitClose { }
    }
}
