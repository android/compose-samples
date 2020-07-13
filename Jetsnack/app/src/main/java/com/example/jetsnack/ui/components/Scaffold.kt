package com.example.jetsnack.ui.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.InnerPadding
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackScaffold(
    //scaffoldState: ScaffoldState = remember { ScaffoldState() },
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    //floatingActionButton: @Composable (() -> Unit)? = null,
    //floatingActionButtonPosition: Scaffold.FabPosition = Scaffold.FabPosition.End,
    //isFloatingActionButtonDocked: Boolean = false,
    //drawerContent: @Composable (() -> Unit)? = null,
    //drawerShape: Shape = MaterialTheme.shapes.large,
    //drawerElevation: Dp = DrawerConstants.DefaultElevation,
    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
    bodyContent: @Composable (InnerPadding) -> Unit
) {
    // Can't wrap scaffold yet b/161450249
    /*Scaffold(
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        backgroundColor = backgroundColor,
        bodyContent = bodyContent
    )*/
    // Currently only need bottomBar so fake it with a Column
    JetsnackSurface(
        color = backgroundColor,
        contentColor = JetsnackTheme.colors.textSecondary
    ) {
        Column {
            if (topBar != null) topBar()
            Box(modifier = Modifier.weight(1f)) {
                bodyContent(InnerPadding())
            }
            if (bottomBar != null) bottomBar()
        }
    }
}
