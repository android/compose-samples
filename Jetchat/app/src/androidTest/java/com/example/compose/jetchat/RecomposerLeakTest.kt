package com.example.compose.jetchat

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class RecomposerLeakTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<NavActivity>()

  @Test
  fun app_launches() {
    ActivityScenario.launch(LeakingActivity::class.java)

    val latchWaitingForNextActivityDestroy = createLatchWaitingForNextActivityDestroy()

    composeTestRule
      .onNodeWithText("Finish")
      .performClick()

    latchWaitingForNextActivityDestroy.await()

    // Uncommenting "composeTestRule.waitForIdle()" fixes the leak and makes the test pass, as this
    // has the side effect of clearing Recomposer.snapshotInvalidations.

    // composeTestRule.waitForIdle()

    // This check fails, LeakingActivity has been finished and destroyed, but it cannot be
    // garbage collected because its held through the lambda passed to ComposeView.setContent()
    // which itself is held by Recomposer.snapshotInvalidations.
    LeakAssertions.assertNoLeaks()
  }

  private fun createLatchWaitingForNextActivityDestroy(): CountDownLatch {
    val latch = CountDownLatch(1)

    val applicationContext =
      InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    applicationContext.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
      override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
      ) = Unit

      override fun onActivityStarted(activity: Activity) = Unit

      override fun onActivityResumed(activity: Activity) = Unit

      override fun onActivityPaused(activity: Activity) = Unit

      override fun onActivityStopped(activity: Activity) = Unit

      override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
      ) = Unit

      override fun onActivityDestroyed(activity: Activity) {
        latch.countDown()
      }
    })
    return latch
  }
}
