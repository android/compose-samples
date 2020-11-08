package com.example.reply

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigator
import kotlinx.android.parcel.Parcelize

sealed class Destination : Parcelable {
    @Parcelize
    object HomeScreen : Destination()

    @Immutable
    @Parcelize
    data class EmailDetail(val index: Long) : Destination()
}

class EmailNavigationActions(navController: NavController) {
    val showDetails: (Long) -> Unit = { emailIndex: Long ->
        navController.navigate(
            R.id.detailScreenFragment,
            bundleOf("index" to emailIndex)
        )
    }
    val backPress: () -> Unit = {
        navController.navigateUp()
    }
}