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

package androidx.compose.samples.crane.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.base.CraneScaffold
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions

private const val DETAILS_NAME = "DETAILS_NAME"
private const val DETAILS_DESCRIPTION = "DETAILS_DESCRIPTION"
private const val DETAILS_LATITUDE = "DETAILS_LATITUDE"
private const val DETAILS_LONGITUDE = "DETAILS_LONGITUDE"

fun launchDetailsActivity(context: Context, item: ExploreModel) {
    context.startActivity(createDetailsActivityIntent(context, item))
}

@VisibleForTesting
fun createDetailsActivityIntent(context: Context, item: ExploreModel): Intent {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra(DETAILS_NAME, item.city.nameToDisplay)
    intent.putExtra(DETAILS_DESCRIPTION, item.description)
    intent.putExtra(DETAILS_LATITUDE, item.city.latitude)
    intent.putExtra(DETAILS_LONGITUDE, item.city.longitude)
    return intent
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
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = args.name,
            style = MaterialTheme.typography.h4
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = args.description,
            style = MaterialTheme.typography.h6
        )
        Spacer(Modifier.preferredHeight(16.dp))
        CityMapView(args.latitude, args.longitude)
    }
}

@Composable
private fun CityMapView(latitude: String, longitude: String) {
    // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
    // with input from Compose UI, those updates are encapsulated into the MapViewContainer
    // composable. In this way, when an update to the MapView happens, this composable won't
    // recompose and the MapView won't need to be recreated.
    val mapView = rememberMapViewWithLifecycle()
    MapViewContainer(mapView, latitude, longitude)
}

@Composable
private fun MapViewContainer(
    map: MapView,
    latitude: String,
    longitude: String
) {
    var zoom by savedInstanceState { InitialZoom }

    ZoomControls(zoom) {
        zoom = it.coerceIn(MinZoom, MaxZoom)
    }
    AndroidView({ map }) { mapView ->
        // Reading zoom so that AndroidView recomposes when it changes. The getMapAsync lambda
        // is stored for later, Compose doesn't recognize state reads
        val mapZoom = zoom
        mapView.getMapAsync {
            it.setZoom(mapZoom)
            val position = LatLng(latitude.toDouble(), longitude.toDouble())
            it.addMarker(
                MarkerOptions().position(position)
            )
            it.moveCamera(CameraUpdateFactory.newLatLng(position))
        }
    }
}

@Composable
private fun ZoomControls(
    zoom: Float,
    onZoomChanged: (Float) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ZoomButton("-", onClick = { onZoomChanged(zoom * 0.8f) })
        ZoomButton("+", onClick = { onZoomChanged(zoom * 1.2f) })
    }
}

@Composable
private fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(8.dp),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.h5)
    }
}

private const val InitialZoom = 5f
const val MinZoom = 2f
const val MaxZoom = 20f
