package com.rs.crypto.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rs.crypto.ui.theme.*
import com.rs.crypto.utils.NavigationItems

@Composable
fun BottomnavigationBar(
    modifier: Modifier = Modifier,
    onItemSelected: (NavigationItems) -> Unit
) {

    val navigationItems = listOf<NavigationItems>(
        NavigationItems.Home,
        NavigationItems.Portfolio,
        NavigationItems.Transaction,
        NavigationItems.Prices,
        NavigationItems.Settings
    )

    var selectedNavItemIndex by remember {
        mutableStateOf(0)
    }

    BottomNavigation(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RectangleShape
            ),
        contentColor = Purple500,
        backgroundColor = Color.White,
    ) {
        navigationItems.forEach{
            var isSelected = selectedNavItemIndex == navigationItems.indexOf(it)
            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    selectedNavItemIndex = navigationItems.indexOf(it)
                    onItemSelected(it)
                },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        if (it.name == NavigationItems.Transaction.name) {

                        } else {
                            Icon(
                                painter = painterResource(id = it.iconRes),
                                contentDescription = it.name,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if(isSelected){
                            Text(
                                text = it.name,
                                style = Typography.h5,
                                maxLines = 1
                            )
                        }
                    }

                },
                selectedContentColor = Purple500,
                unselectedContentColor = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomnavigationBar() {

    }
}