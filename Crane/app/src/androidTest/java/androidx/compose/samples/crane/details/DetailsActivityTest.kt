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

import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.MADRID
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.test.AndroidComposeTestRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.onNodeWithText
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class DetailsActivityTest {

    private val expectedDescription = "description"
    private val testExploreModel = ExploreModel(MADRID, expectedDescription, "imageUrl")

    @get:Rule
    val composeTestRule = AndroidComposeTestRule(
        ActivityScenarioRule<DetailsActivity>(
            createDetailsActivityIntent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                testExploreModel
            )
        )
    )

    @Test
    fun mapView_cameraPositioned() {
        composeTestRule.onNodeWithText(MADRID.nameToDisplay).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedDescription).assertIsDisplayed()
        onView(withId(R.id.map)).check(matches(isDisplayed()))

        var cameraPosition: CameraPosition? = null
        waitForMap(onCameraPosition = { cameraPosition = it })

        val expected = LatLng(
            testExploreModel.city.latitude.toDouble(),
            testExploreModel.city.longitude.toDouble()
        )
        assert(expected.latitude == cameraPosition?.target?.latitude?.round(6))
        assert(expected.longitude == cameraPosition?.target?.longitude?.round(6))
    }

    /**
     * As the MapView is included using the AndroidView API, it cannot be referenced using Compose
     * testing APIs. Therefore, we use the activityRule to get an instance of the DetailsActivity
     * an findViewById using MapView's id.
     *
     * As obtaining the map is an asynchronous call, we use a CountDownLatch to make this
     * call synchronous in the test.
     */
    private fun waitForMap(onCameraPosition: (CameraPosition) -> Unit) {
        val countDownLatch = CountDownLatch(1)
        composeTestRule.activityRule.scenario.onActivity {
            it.findViewById<MapView>(R.id.map).getMapAsync { map ->
                onCameraPosition(map.cameraPosition)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
    }
}

private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
