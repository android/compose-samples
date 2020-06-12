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

package androidx.compose.samples.crane.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneScaffold
import androidx.compose.samples.crane.data.ExploreModel
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import androidx.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val DETAILS_NAME = "DETAILS_NAME"
private const val DETAILS_DESCRIPTION = "DETAILS_DESCRIPTION"
private const val DETAILS_LATITUDE = "DETAILS_LATITUDE"
private const val DETAILS_LONGITUDE = "DETAILS_LONGITUDE"

fun launchDetailsActivity(context: Context, item: ExploreModel) {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra(DETAILS_NAME, item.city.nameToDisplay)
    intent.putExtra(DETAILS_DESCRIPTION, item.description)
    intent.putExtra(DETAILS_LATITUDE, item.city.latitude)
    intent.putExtra(DETAILS_LONGITUDE, item.city.longitude)
    context.startActivity(intent)
}

data class DetailsActivityArg(
    val name: String,
    val description: String,
    val latitude: String,
    val longitude: String
)

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = DetailsActivityArg(
            name = intent.getStringExtra(DETAILS_NAME)!!,
            description = intent.getStringExtra(DETAILS_DESCRIPTION)!!,
            latitude = intent.getStringExtra(DETAILS_LATITUDE)!!,
            longitude = intent.getStringExtra(DETAILS_LONGITUDE)!!
        )

        setContent {
            CraneScaffold {
                DetailsScreen(args = args)
            }
        }
    }
}

@Composable
fun DetailsScreen(args: DetailsActivityArg) {
    Column(verticalArrangement = Arrangement.Center) {
        Spacer(Modifier.preferredHeight(32.dp))
        Text(
            modifier = Modifier.gravity(Alignment.CenterHorizontally),
            text = args.name,
            style = MaterialTheme.typography.h4
        )
        Text(
            modifier = Modifier.gravity(Alignment.CenterHorizontally),
            text = args.description,
            style = MaterialTheme.typography.h6
        )
        Spacer(Modifier.preferredHeight(16.dp))
        Box(Modifier.padding(all = 8.dp)) {
            // Interop between Compose and the Android UI toolkit as we need to show
            // a MapView which extends an androidx Fragment

            // For the map to work, you need to get an API key from
            // https://developers.google.com/maps/documentation/android-sdk/get-api-key
            // and put it in the google.maps.key variable of your local properties
            AndroidView(resId = R.layout.layout_details_map, postInflationCallback = { view ->
                val fragment = (view.context as AppCompatActivity).supportFragmentManager
                    .findFragmentById(R.id.map)

                (fragment as SupportMapFragment).getMapAsync { map ->
                    val position = LatLng(args.latitude.toDouble(), args.longitude.toDouble())
                    map.addMarker(MarkerOptions().position(position).title("Marker in ${args.name}"))
                    map.moveCamera(CameraUpdateFactory.newLatLng(position))
                }
            })
        }
    }
}
