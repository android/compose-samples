package com.baidaidai.animora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.baidaidai.animora.components.info.infoScreen
import com.baidaidai.animora.ui.theme.TestAppTheme

class InfoActivity : ComponentActivity() {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterial3ExpressiveApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                infoScreen()
            }
        }
    }
}