package com.example.jetsnack.widget.utils

import androidx.compose.runtime.Composable
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import com.example.platform.ui.appwidgets.glance.layout.ActionDemonstrationActivity
import com.example.platform.ui.appwidgets.glance.layout.ActionSourceMessageKey

/**
 * Utility functions for creating [Action]s.
 */
object ActionUtils {
  /**
   * [Action] for launching the [ActionDemonstrationActivity] with the given message.
   */
  @Composable
  fun actionStartDemoActivity(message: String) =
    actionStartActivity<ActionDemonstrationActivity>(
      actionParametersOf(
        ActionSourceMessageKey to message
      )
    )
}