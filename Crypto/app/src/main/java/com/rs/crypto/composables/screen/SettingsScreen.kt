package com.rs.crypto.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.ui.theme.*
import com.rs.crypto.R
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun SettingsScreen(
    onBackArrowPressed: () -> Unit,
    ) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Secondary
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 50.dp)
        ) {
            TopNavigationRow(
                onBackArrowPressed = onBackArrowPressed,
                isStarNeeded = false
            )

            Spacer(modifier = Modifier.height((2*Constants.PADDING_SIDE_VALUE).dp))

            UserInfoSection()

            SettingsColumn()
        }
    }
}

@Composable
private fun SettingsColumn() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp)
    ) {

        SettingsItem(
            iconImageVector = Icons.Default.Person,
            text = "Profile"
        )

        SettingsItem(
            iconImageVector = Icons.Default.Tune,
            text = "Preferences"
        )

        SettingsItem(
            iconImageVector = Icons.Default.Notifications,
            text = "Notifications"
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.PADDING_SIDE_VALUE.dp)
        )

        SettingsItem(
            iconImageVector = Icons.Default.Security,
            text = "Security"
        )

        SettingsItem(
            iconImageVector = Icons.Default.Lock,
            text = "Privacy"
        )

        SettingsItem(
            iconImageVector = Icons.Default.Info,
            text = "About"
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Constants.PADDING_SIDE_VALUE.dp)
        )

        SettingsItem(
            iconImageVector = Icons.Default.Logout,
            text = "Logout"
        )
    }
}

@Composable
private fun SettingsItem(
    iconImageVector: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        IconTextRow(
            iconImageVector = iconImageVector,
            text = text
        )

        Image(
            painter = painterResource(id = R.drawable.right_arrow),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color.White),
            modifier = Modifier
                .clipToBounds()
                .padding(horizontal = (Constants.PADDING_SIDE_VALUE * 1.5).dp)
                .size(Constants.PADDING_SIDE_VALUE.dp)
        )
    }
}

@Composable
private fun IconTextRow(
    iconImageVector: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = Constants.PADDING_SIDE_VALUE.dp)
    ) {
        Icon(
            imageVector = iconImageVector,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier
                .size((2*Constants.PADDING_SIDE_VALUE).dp)
        )

        Spacer(modifier = Modifier.width(Constants.ELEVATION_VALUE.dp))

        Text(
            text = text,
            style = TextStyle(
                fontFamily = RobotoRegular,
                fontSize = 14.sp,
                lineHeight = 22.sp
            ),
            color = Color.White
        )
    }
}

@Composable
private fun UserInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${DummyData.user.firstName} ${DummyData.user.lastName}",
            style = Typography.h2,
            color = Color.White
        )

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Image",
            tint = Color.White,
            modifier = Modifier
                .size((4 * Constants.PADDING_SIDE_VALUE).dp)
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        onBackArrowPressed = {

        }
    )
}