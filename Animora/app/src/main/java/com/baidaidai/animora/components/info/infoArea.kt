package com.baidaidai.animora.components.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.R
import kotlinx.coroutines.launch

/**
 * “关于”页面中的应用信息区域容器。
 *
 * 这个 Composable 函数以卡片的形式展示了应用信息，
 * 包括应用 Logo、名称、版本号以及一个指向 Github 的链接。
 *
 * @param onClick 当点击 "View in Github" 时触发的回调。
 */
@Composable
fun infoAreaContainer(
    onClick: ()->Unit
){
    val coroutineScope = rememberCoroutineScope()
    OutlinedCard {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp),
            ){
                Image(
                    painter = painterResource(R.drawable.animation_icon),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            color = Color(0xD1,0xB6,0xE4,0xFF)
                        )
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(start = 15.dp)
                        .wrapContentSize(),
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
            )
            ListItem(
                headlineContent = {
                    Text("Version")
                },
                supportingContent = {
                    Text(
                        text = stringResource(R.string.app_version_local)
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_apk_document_24),
                        contentDescription = "Application Version Icons"
                    )
                }
            )
            ListItem(
                headlineContent = {
                    Text("View in Github")
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "View in Github",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp)
                    )
                },
                modifier = Modifier
                    .clickable(
                        onClick = onClick
                    )
            )
        }
    }
}