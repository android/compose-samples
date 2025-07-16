package com.example.benchmark.helper

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiObject2Condition
import androidx.test.uiautomator.Until
import java.io.ByteArrayOutputStream

const val TARGET_PACKAGE_NAME = "com.example.jetcaster"


fun UiDevice.dumpWindowHierarchy(): String {
    val buffer = ByteArrayOutputStream()
    dumpWindowHierarchy(buffer)
    return buffer.toString()
}

const val HOME_CONTENT_GRID_TAG = "home_content_grid"

fun MacrobenchmarkScope.forContentGrid() {
    // Wait until content is loaded by checking if topics are loaded
    device.wait(Until.gone(By.res(HOME_CONTENT_GRID_TAG)), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for topics to be sure
    val obj = device.waitAndFindObject(By.res(HOME_CONTENT_GRID_TAG), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    obj.wait(untilHasChildren(), 60_000)
}

fun UiDevice.waitAndFindObject(selector: BySelector, timeout: Long): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout)) {
        throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
    }

    return findObject(selector)
}

private fun untilHasChildren(
    childCount: Int = 1,
    op: HasChildrenOp = HasChildrenOp.AT_LEAST,
): UiObject2Condition<Boolean> = object : UiObject2Condition<Boolean>() {
    override fun apply(element: UiObject2): Boolean = when (op) {
        HasChildrenOp.AT_LEAST -> element.childCount >= childCount
        HasChildrenOp.EXACTLY -> element.childCount == childCount
        HasChildrenOp.AT_MOST -> element.childCount <= childCount
    }
}

enum class HasChildrenOp {
    AT_LEAST,
    EXACTLY,
    AT_MOST,
}