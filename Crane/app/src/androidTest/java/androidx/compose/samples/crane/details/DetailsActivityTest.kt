/*
 * Copyright 2022 The Android Open Source Project
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

import androidx.compose.foundation.layout.Column
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.MADRID
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.pow

@HiltAndroidTest
class DetailsActivityTest {
    @Inject
    lateinit var destinationsRepository: DestinationsRepository

    private lateinit var cityDetails: ExploreModel

    private val city = MADRID
    private val testExploreModel = ExploreModel(city, "description", "imageUrl")

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var mapState: CameraPositionState

    @Before
    fun setUp() {
        hiltRule.inject()

        cityDetails = destinationsRepository.getDestination(MADRID.name)!!
        mapState = CameraPositionState()

        val countDownLatch = CountDownLatch(1)

        composeTestRule.setContent {
            CraneTheme {
                Column {
                    CityMapView(
                        latitude = testExploreModel.city.latitude,
                        longitude = testExploreModel.city.longitude,
                        cameraState = mapState,
                        onMapLoaded = {
                            countDownLatch.countDown()
                        }
                    )
                }
            }
        }

        countDownLatch.await()
    }

    @Test
    fun mapView_cameraPositioned() {
        val expected = LatLng(
            testExploreModel.city.latitude.toDouble(),
            testExploreModel.city.longitude.toDouble()
        )
        assertTrue(expected.latitude == mapState.position.target.latitude.round(6))
        assertTrue(expected.longitude == mapState.position.target.longitude.round(6))
    }

    @Test
    fun mapView_zoomIn() {
        val zoomBefore = mapState.position.zoom
        composeTestRule.onNodeWithText("+")
            .assertIsDisplayed()
            .performClick()

        assertTrue(zoomBefore < mapState.position.zoom)
    }

    @Test
    fun mapView_zoomOut() {
        val zoomBefore = mapState.position.zoom
        composeTestRule.onNodeWithText("-")
            .assertIsDisplayed()
            .performClick()

        assertTrue(zoomBefore > mapState.position.zoom)
    }
}

private fun Double.round(decimals: Int = 2): Double =
    kotlin.math.round(this * 10f.pow(decimals)) / 10f.pow(decimals)
