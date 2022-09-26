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
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.MADRID
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlin.math.pow
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("To be fixed in https://github.com/android/compose-samples/issues/746")
@HiltAndroidTest
class CityMapViewTests {
    @Inject
    lateinit var destinationsRepository: DestinationsRepository

    private lateinit var cityDetails: City

    private val city = MADRID
    private val testExploreModel = ExploreModel(city, "description", "imageUrl")

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    // Using MainActivity so we can initialise Hilt but not have to populate savedstate for
    // unused viewmodels.
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mapState: CameraPositionState
    private lateinit var zoomLatch: CountDownLatch

    @Before
    fun setUp() {
        hiltRule.inject()

        cityDetails = destinationsRepository.getDestination(MADRID.name)!!

        val countDownLatch = CountDownLatch(1)
        zoomLatch = CountDownLatch(1)

        composeTestRule.setContent {
            CraneTheme {
                Column {
                    CityMapView(
                        latitude = testExploreModel.city.latitude,
                        longitude = testExploreModel.city.longitude,
                        onMapLoadedWithCameraState = { cameraPositionState ->
                            mapState = cameraPositionState
                            countDownLatch.countDown()
                        },
                        onZoomChanged = { zoomLatch.countDown() }
                    )
                }
            }
        }

        assertTrue("Map failed to load in time.", countDownLatch.await(30, TimeUnit.SECONDS))
    }

    @Test
    fun mapView_cameraPositioned() {
        val expected = LatLng(
            testExploreModel.city.latitude.toDouble(),
            testExploreModel.city.longitude.toDouble()
        )

        assertEquals(expected.latitude, mapState.position.target.latitude.round(6))
        assertEquals(expected.longitude, mapState.position.target.longitude.round(6))
    }

    @Test
    fun mapView_zoomIn() {
        val zoomBefore = mapState.position.zoom
        composeTestRule.onNodeWithText("+")
            .assertIsDisplayed()
            .performClick()

        // Wait for the animation to happen
        assertTrue("Zoom timed out", zoomLatch.await(30, TimeUnit.SECONDS))

        assertTrue(zoomBefore < mapState.position.zoom)
    }

    @Test
    fun mapView_zoomOut() {
        val zoomBefore = mapState.position.zoom
        composeTestRule.onNodeWithText("-")
            .assertIsDisplayed()
            .performClick()

        // Wait for the animation to happen
        assertTrue("Zoom timed out", zoomLatch.await(30, TimeUnit.SECONDS))

        assertTrue(zoomBefore > mapState.position.zoom)
    }
}

private fun Double.round(decimals: Int = 2): Double =
    kotlin.math.round(this * 10f.pow(decimals)) / 10f.pow(decimals)
