package androidx.compose.samples.crane.base

import androidx.compose.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

private val screens = listOf("Find Trips", "My Trips", "Saved Trips", "Price Alerts", "My Account")

@Composable
fun CraneDrawer(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(start = 24.dp, top = 48.dp)) {
        Image(asset = vectorResource(id = R.drawable.ic_crane_drawer))
        for (screen in screens) {
            Spacer(Modifier.preferredHeight(24.dp))
            Text(text = screen, style = MaterialTheme.typography.h4)
        }
    }
}

@Preview
@Composable
fun CraneDrawerPreview() {
    CraneTheme {
        CraneDrawer()
    }
}
