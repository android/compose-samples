package com.baidaidai.animora.components.info.infoScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

object NecessaryComponents {
    /**
     * “关于”页面的顶部应用栏（TopAppBar）。
     *
     * 这个 Composable 包含一个返回按钮和一个固定的标题 "About"。
     *
     * @param onClick 返回按钮的点击事件回调。
     */
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterial3ExpressiveApi::class
    )
    @Composable
    fun infoScreenTopAppBar(
        onClick: ()-> Unit
    ){
        TopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            },
            title = {
                Text("About")
            }
        )
    }
}