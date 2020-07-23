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

package androidx.compose.samples.crane.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onCommit
import androidx.compose.samples.crane.R
import androidx.compose.setValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.preferredSize
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

@Composable
fun NetworkImage(url: String, width: Dp, height: Dp) {
    var image by mutableStateOf<ImageAsset?>(null)

    onCommit(url) {
        val picasso = Picasso.get()
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                image = bitmap?.asImageAsset()
            }
        }
        picasso
            .load(url)
            .into(target)

        onDispose {
            image = null
            picasso.cancelRequest(target)
        }
    }
    Surface(
        modifier = Modifier.preferredSize(width = width, height = height),
        shape = RoundedCornerShape(4.dp)
    ) {
        val loadedImage = image
        if (loadedImage != null) {
            Image(asset = loadedImage)
        } else {
            Image(asset = vectorResource(id = R.drawable.ic_crane_logo))
        }
    }
}
