package com.example.jetnews.util

import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.Composable
import androidx.test.rule.ActivityTestRule
import androidx.ui.core.Density
import androidx.ui.core.setContent
import androidx.ui.test.ComposeTestCase
import androidx.ui.test.ComposeTestCaseSetup
import androidx.ui.test.ComposeTestRule
import androidx.ui.test.android.AndroidComposeTestCaseSetup
import com.example.jetnews.ui.MainActivity
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Jetnews specific implementation of [ComposeTestRule].
 */
class JetnewsComposeTestRule : ComposeTestRule {

    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    override val density: Density get() = Density(activityTestRule.activity)

    override val displayMetrics: DisplayMetrics
        get() =
            activityTestRule.activity.resources.displayMetrics

    override fun apply(base: Statement, description: Description?): Statement {
        return activityTestRule.apply(AndroidComposeStatement(base), description)
    }

    override fun runOnUiThread(action: () -> Unit) {
        // Workaround for lambda bug in IR
        activityTestRule.activity.runOnUiThread(object : Runnable {
            override fun run() {
                action.invoke()
            }
        })
    }

    /**
     * Use this in your tests to setup the UI content to be tested. This should be called exactly
     * once per test.
     */
    @SuppressWarnings("SyntheticAccessor")
    override fun setContent(composable: @Composable() () -> Unit) {
        val drawLatch = CountDownLatch(1)
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                drawLatch.countDown()
                val contentViewGroup =
                    activityTestRule.activity.findViewById<ViewGroup>(android.R.id.content)
                contentViewGroup.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        val runnable: Runnable = object : Runnable {
            override fun run() {
                activityTestRule.activity.setContent(composable)
                val contentViewGroup =
                    activityTestRule.activity.findViewById<ViewGroup>(android.R.id.content)
                contentViewGroup.viewTreeObserver.addOnGlobalLayoutListener(listener)
            }
        }
        activityTestRule.runOnUiThread(runnable)
        drawLatch.await(1, TimeUnit.SECONDS)
    }

    override fun forGivenContent(composable: @Composable() () -> Unit): ComposeTestCaseSetup {
        val testCase = object : ComposeTestCase {
            @Composable
            override fun emitContent() {
                composable()
            }
        }
        return AndroidComposeTestCaseSetup(
            this,
            testCase,
            activityTestRule.activity
        )
    }

    override fun forGivenTestCase(testCase: ComposeTestCase): ComposeTestCaseSetup {
        return AndroidComposeTestCaseSetup(
            this,
            testCase,
            activityTestRule.activity
        )
    }

    inner class AndroidComposeStatement(
        private val base: Statement
    ) : Statement() {
        override fun evaluate() {
            base.evaluate()
        }
    }
}
