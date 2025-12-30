package com.baidaidai.animora.components.StartScreen.list.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.baidaidai.animora.LocalAnimatedContentScope
import com.baidaidai.animora.LocalSharedTransitionScope
import com.baidaidai.animora.shared.dataClass.AnimationDatas
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun animationListItem(
    animationList: AnimationDatas,
    listOnClick:suspend()-> Unit,
    questionOnClick:suspend()-> Unit,
){
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    val coroutineScope = rememberCoroutineScope()
    with(sharedTransitionScope){
        ListItem(
            headlineContent = {
                Text(
                    text = animationList.name,
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState("AnimationTitle-${animationList.name}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
            },
            trailingContent = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            questionOnClick()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.QuestionMark, contentDescription = "Question")
                }
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(
                    onClick = {
                        coroutineScope.launch {
                            listOnClick()
                        }
                    }
                )
        )
    }
}