package com.example.compose.jetchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView

class LeakingActivity : AppCompatActivity() {

  private lateinit var composeView: ComposeView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    composeView = ComposeView(this)
    resetContent()
    setContentView(composeView)
  }

  private fun resetContent() {
    composeView.setContent {
      Button(onClick = {
        // The lambda passed to ComposeView.setContent() is added to Recomposer.snapshotInvalidations
        // This wouldn't be a problem when the Recomposer is per activity. However, in test,
        // there is a single shared Recomposer across all activities.
        resetContent()
        finish()
      }) {
        Text("Finish")
      }
    }
  }
}
