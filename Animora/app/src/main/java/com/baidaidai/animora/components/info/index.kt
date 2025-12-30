package com.baidaidai.animora.components.info

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.R
import com.baidaidai.animora.components.info.infoScreen.NecessaryComponents

/**
 * Animora 应用的 Info 页面容器型 Composable。
 *
 * 该函数负责展示应用信息，包括：
 * - 应用版本号和项目地址
 * - 作者介绍及社交链接（GitHub、Twitter）
 *
 * @see Scaffold
 * @see LocalUriHandler
 */
@Composable
fun infoScreen(){
    val activity = LocalContext.current as Activity
    val uriHandler = LocalUriHandler.current as UriHandler
    Scaffold(
        topBar = {
            NecessaryComponents.infoScreenTopAppBar {
                activity.finish()
            }
        }
    ){ contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 20.dp)
        ){
            infoAreaContainer {
                uriHandler.openUri(
                    uri = "https://github.com/Baidaidai-GFWD-origin/Animora"
                )
            }
            Spacer(modifier = Modifier.size(height = 10.dp, width = 1.dp))
            authorAreaContainer(
                onClickGithub = {
                    uriHandler.openUri(
                        uri = "https://github.com/Baidaidai-GFWD-origin"
                    )
                },
                onClickTwitter = {
                    uriHandler.openUri(
                        uri = "https://x.com/creater_bai"
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun infoScreenPreview(){
    infoScreen()
}