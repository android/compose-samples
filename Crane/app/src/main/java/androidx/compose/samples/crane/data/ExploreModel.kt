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

package androidx.compose.samples.crane.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.Immutable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

@Immutable
data class City(
    val name: String,
    val country: String,
    val latitude: String,
    val longitude: String
) {
    val nameToDisplay = "$name, $country"
}

@Immutable
data class ExploreModel(
    val city: City,
    val description: String,
    val imageUrl: String
)

class ExploreUiModel(val exploreModel: ExploreModel) {

    var image by mutableStateOf<ImageAsset?>(null)
        private set

    init {
        val picasso = Picasso.get()
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                image = bitmap?.asImageAsset()
            }
        }
        picasso
            .load(exploreModel.imageUrl)
            .into(target)
    }
}
