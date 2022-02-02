package com.example.jetsnack.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Ignore

/**
 * Created by André Schabrocker on 2022-01-31
 */
@Ignore abstract class BaseUITestHelper {
    val applicationContext: Context = ApplicationProvider.getApplicationContext()
}
