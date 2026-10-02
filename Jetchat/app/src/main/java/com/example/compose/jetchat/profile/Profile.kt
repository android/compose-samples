/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.profile

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.blur.backdropBlur
import com.example.compose.jetchat.components.AnimatingFabContent
import com.example.compose.jetchat.components.baselineHeight
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.theme.Cookie9Sided
import com.example.compose.jetchat.theme.FullScreenRoundedRectangle
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.theme.SharedElementKey
import com.example.compose.jetchat.theme.sharedAvatarElement
import com.example.compose.jetchat.theme.sharedElementTransitionSpec

@Composable
fun ProfileScreen(userData: ProfileScreenState, sharedElementKey: String? = null) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    val scrollState = rememberScrollState()
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    val animatedBlur by animatedVisibilityScope.transition.animateDp(
        transitionSpec = { MaterialTheme.motionScheme.sharedElementTransitionSpec() },
        label = "profileBackdropBlur",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> 0.dp
            EnterExitState.Visible -> 32.dp
            EnterExitState.PostExit -> 0.dp
        }
    }
    val blurRadius =  animatedBlur.coerceAtLeast(0.dp)



    val animatedAlpha by animatedVisibilityScope.transition.animateFloat(
        transitionSpec = { MaterialTheme.motionScheme.sharedElementTransitionSpec() },
        label = "profileContentAlpha",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> 0f
            EnterExitState.Visible -> 1f
            EnterExitState.PostExit -> 0f
        }
    }
    val contentAlpha =animatedAlpha.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .backdropBlur(radius = blurRadius),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = contentAlpha),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                ProfileHeader(
                    data = userData,
                    sharedElementKey = sharedElementKey,
                )
                UserInfoFields(
                    userData = userData,
                    modifier = Modifier.graphicsLayer { this.alpha = contentAlpha },
                )
            }
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.navigationBarsPadding()) {
        Spacer(modifier = Modifier.height(40.dp))

        NameAndPosition(userData)

        ProfileProperty(stringResource(R.string.display_name), userData.displayName)

        ProfileProperty(stringResource(R.string.status), userData.status)

        ProfileProperty(stringResource(R.string.twitter), userData.twitter, isLink = true)

        userData.timeZone?.let {
            ProfileProperty(stringResource(R.string.timezone), userData.timeZone)
        }
    }
}

@Composable
private fun NameAndPosition(userData: ProfileScreenState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData,
            modifier = Modifier.baselineHeight(32.dp),
        )
        Position(
            userData,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .baselineHeight(24.dp),
        )
    }
}

@Composable
private fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun Position(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.position,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileHeader(data: ProfileScreenState, sharedElementKey: String? = null) {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    val animatedRadius by animatedVisibilityScope.transition.animateDp(
        transitionSpec = { MaterialTheme.motionScheme.sharedElementTransitionSpec() },
        label = "profileProgressiveBlur",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> 0.dp
            EnterExitState.Visible -> 32.dp
            EnterExitState.PostExit -> 0.dp
        }
    }
     val progressiveBlurRadius = animatedRadius.coerceAtLeast(0.dp)


    data.photo?.let {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .sharedAvatarElement(
                    sharedElementKey = sharedElementKey?.let { key -> SharedElementKey.ProfileAvatar(key) },
                    restingShape = FullScreenRoundedRectangle,
                    targetShape = Cookie9Sided,
                )
                .blur {
                    blurRadiusSpec = BlurRadiusSpec.verticalGradient(
                        listOf(
                            BlurStop(0.5f, 0.dp),
                            BlurStop(1.0f, progressiveBlurRadius),
                        ),
                    )
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                },
            painter = painterResource(id = it),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        HorizontalDivider()
        Text(
            text = label,
            modifier = Modifier.baselineHeight(24.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val style = if (isLink) {
            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        Text(
            text = value,
            modifier = Modifier.baselineHeight(24.dp),
            style = style,
        )
    }
}

@Composable
fun ProfileError() {
    Text(stringResource(R.string.profile_error))
}

@Composable
fun ProfileFab(extended: Boolean, userIsMe: Boolean, modifier: Modifier = Modifier, onFabClicked: () -> Unit = { }) {
    key(userIsMe) {
        // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            onClick = onFabClicked,
            modifier = modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .height(48.dp)
                .widthIn(min = 48.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        painter = painterResource(id = if (userIsMe) R.drawable.ic_create else R.drawable.ic_chat),
                        contentDescription = stringResource(
                            if (userIsMe) R.string.edit_profile else R.string.message,
                        ),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.message,
                        ),
                    )
                },
                extended = extended,
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ConvPreviewLandscapeMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitOtherDefault() {
    JetchatTheme {
        ProfileScreen(colleagueProfile)
    }
}

@Preview
@Composable
fun ProfileFabPreview() {
    JetchatTheme {
        ProfileFab(extended = true, userIsMe = false)
    }
}
