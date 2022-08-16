package com.rs.crypto

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import com.rs.crypto.composables.*
import com.rs.crypto.ui.theme.CryptocurrencyAppTheme
import com.rs.crypto.ui.theme.Purple500
import com.rs.crypto.utils.NavigationItems
import com.rs.crypto.utils.Screen
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptocurrencyAppTheme {
                TradeModelBottomSheet(
                    onButtonClick = {
                        Toast
                            .makeText(this, "Processing transaction...", Toast.LENGTH_LONG)
                            .show()
                    }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainActivityContent(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomnavigationBar(
                onItemSelected = {
                    when (it) {
                        NavigationItems.Home -> navController.navigate(Screen.HomeScreen.route)
                        NavigationItems.Portfolio -> navController.navigate(Screen.PortfolioScreen.route)
                        NavigationItems.Prices -> navController.navigate(Screen.PricesScreen.route)
                        NavigationItems.Settings -> navController.navigate(Screen.SettingsScreen.route)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch{
                        modalBottomSheetState.show()
                    }
                },
                backgroundColor = Purple500,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.transaction),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        drawerShape = MaterialTheme.shapes.small,
        modifier = Modifier.shadow(
            elevation = 10.dp,
            shape = RectangleShape
        )
    ) {
        Navigation(navController)
    }
}


@Composable
@ExperimentalMaterialApi
private fun TradeModelBottomSheet(
    onButtonClick: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue =ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            TradeScreen(
                onButtonClick = {
                    onButtonClick()
                }
            )
        },
        content = {
            MainActivityContent(scope, modalBottomSheetState)
        }
    )
}