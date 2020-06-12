/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.home

import android.os.Handler
import android.os.Looper
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.onCommit
import androidx.compose.samples.crane.R
import androidx.core.os.postDelayed
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Image
import androidx.ui.layout.fillMaxSize
import androidx.ui.res.vectorResource

private const val SPLASH_WAIT_TIME: Long = 2000

@Composable
fun LandingScreen(modifier: Modifier = Modifier, splashShownState: MutableState<SplashState>) {
    Box(modifier = modifier.fillMaxSize(), gravity = ContentGravity.Center) {
        onCommit {
            Handler(Looper.getMainLooper()).postDelayed(SPLASH_WAIT_TIME) {
                splashShownState.value = SplashState.COMPLETED
            }
        }
        Image(asset = vectorResource(id = R.drawable.ic_crane_drawer))
    }
}
