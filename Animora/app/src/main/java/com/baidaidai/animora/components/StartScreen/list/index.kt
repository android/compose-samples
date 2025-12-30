package com.baidaidai.animora.components.StartScreen.list

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baidaidai.animora.LocalAnimationViewModel
import com.baidaidai.animora.components.StartScreen.list.components.animationListItem
import com.baidaidai.animora.components.StartScreen.list.components.modalBottomSheet
import com.baidaidai.animora.components.StartScreen.list.components.principledHeader
import com.baidaidai.animora.components.StartScreen.list.model.animationList
import com.baidaidai.animora.components.StartScreen.model.homeScreenBlurViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun animationListContainer(
    contentPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: homeScreenBlurViewModel,
){

    val animationDatasViewModel = LocalAnimationViewModel.current
    var bottomSheetState by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val homeScreenBlurViewModel = viewModel

    @StringRes
    var bottomSheetContent: Int by rememberSaveable { mutableIntStateOf(0) }

    if (bottomSheetState){
        modalBottomSheet(
            onDismissRequest = {
                modalBottomSheetState.hide()
                bottomSheetState = !bottomSheetState
                homeScreenBlurViewModel.changeBlurStatus(bottomSheetState)
            },
            modalBottomSheetState = modalBottomSheetState,
            bottomSheetContent = bottomSheetContent
        )
    }
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(contentPaddingValues)
    ) {
        LazyColumn {
            items(
                items = animationList,
                key = { animationList ->
                    animationList.id
                }
            ) { animationList->
                principledHeader(animationList)
                Column(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    animationListItem(
                        animationList = animationList,
                        listOnClick = {
                            animationDatasViewModel.changeSelectedAnimation(
                                animationDatas = animationList
                            )
                            navController.navigate("Detail")
                        },
                        questionOnClick = {
                            bottomSheetContent = animationList.shortInfo
                            coroutineScope.launch {
                                bottomSheetState = !bottomSheetState
                                homeScreenBlurViewModel.changeBlurStatus(bottomSheetState)
                                delay(500)
                                modalBottomSheetState.show()
                            }
                        }
                    )
                }
            }
        }
    }

}