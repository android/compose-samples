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

package androidx.compose.samples.crane.base

import androidx.compose.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.BottomSheetShape
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.samples.crane.ui.crane_divider_color
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White, shape = BottomSheetShape) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
            Spacer(Modifier.preferredHeight(8.dp))
            LazyColumnItems(
                modifier = Modifier.weight(1f),
                items = exploreList
            ) { item ->
                ExploreItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    onItemClicked = onItemClicked
                )
                Divider(color = crane_divider_color)
            }
        }
    }
}

@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer {
            CoilImageWithCrossfade(
                data = item.imageUrl,
                contentScale = ContentScale.Crop,
                loading = {
                    Stack(Modifier.fillMaxSize()) {
                        Image(
                            modifier = Modifier.preferredSize(36.dp).gravity(Alignment.Center),
                            asset = vectorResource(id = R.drawable.ic_crane_logo)
                        )
                    }
                },
                modifier = modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.preferredWidth(24.dp))
        Column {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.preferredHeight(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreImageContainer(children: @Composable () -> Unit) {
    Surface(Modifier.preferredSize(width = 60.dp, height = 60.dp), RoundedCornerShape(4.dp)) {
        children()
    }
}
