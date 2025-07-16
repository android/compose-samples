package com.example.benchmark.baselineprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.example.benchmark.helper.HOME_CONTENT_GRID_TAG
import com.example.benchmark.helper.TARGET_PACKAGE_NAME
import com.example.benchmark.helper.forContentGrid
import com.example.benchmark.helper.waitAndFindObject
import org.junit.Rule
import org.junit.Test

@RequiresApi(Build.VERSION_CODES.P)
class StartupBaselineProfile() { // Assumes BaselineProfileGenerator is defined (e.g., in StartupBaselineProfile.kt or a separate file)

    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(TARGET_PACKAGE_NAME) {
        pressHome()
        startActivityAndWait() // Waits for the first frame of the Activity to be drawn

        // 1. Wait for the scrollable home content grid to appear.
        // UI Automator uses resource IDs for testTags.
        // Using the string value directly or the imported constant.
        forContentGrid()
        val homeGrid = device.waitAndFindObject(
            By.res(HOME_CONTENT_GRID_TAG),
            5_000,
        )
        // 2. Perform a scroll action on the grid.
        // Set gesture margin to avoid triggering system gestures if the grid is near screen edges.
        homeGrid.setGestureMargin(device.displayWidth / 5) // Adjust margin as needed

        // Fling downwards. For a LazyVerticalGrid, DOWN is the primary scroll direction.
        homeGrid.fling(Direction.DOWN)



        // If you need to scroll horizontally (e.g., if it's a LazyHorizontalGrid or a grid within a pager)
        // you would use Direction.LEFT or Direction.RIGHT.
        // For a vertical grid, Direction.DOWN or Direction.UP are appropriate.

        // 3. Wait for the UI to settle after the scroll.
        device.waitForIdle()
    }
}