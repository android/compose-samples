package com.example.jetsnack.util

import android.content.Context
import androidx.annotation.CallSuper
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.jetsnack.ui.MainActivity
import org.junit.*

/**
 * Created by André Schabrocker on 2022-01-31
 */
@Ignore abstract class BaseUITestHelper {
    val applicationContext: Context = ApplicationProvider.getApplicationContext()

}