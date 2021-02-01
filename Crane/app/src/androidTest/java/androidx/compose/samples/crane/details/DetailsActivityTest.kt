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
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.MADRID
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round

@HiltAndroidTest
class DetailsActivityTest {

    @Inject
    lateinit var destinationsRepository: DestinationsRepository
    lateinit var cityDetails: ExploreModel

    private val city = MADRID
    private val testExploreModel = ExploreModel(city, "description", "imageUrl")

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = AndroidComposeTestRule(
        activityRule = ActivityScenarioRule<DetailsActivity>(
            createDetailsActivityIntent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                testExploreModel
            )
        ),
        // Needed for now, discussed in https://issuetracker.google.com/issues/174472899
        activityProvider = { rule ->
            var activity: DetailsActivity? = null
            rule.scenario.onActivity { activity = it }
            if (activity == null) {
                throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
            }
            activity!!
        }
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        cityDetails = destinationsRepository.getDestination(MADRID.name)!!
    }

    @Test
    fun mapView_cameraPositioned() {
        composeTestRule.onNodeWithText(cityDetails.city.nameToDisplay).assertIsDisplayed()
        composeTestRule.onNodeWithText(cityDetails.description).assertIsDisplayed()
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

private fun Double.round(decimals: Int = 2): Double =
    round(this * 10f.pow(decimals)) / 10f.pow(decimals)
