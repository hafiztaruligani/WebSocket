package com.ht.websocket.presentation.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ht.websocket.presentation.ChatScreenRoute
import com.ht.websocket.presentation.HomeScreenRoute
import com.ht.websocket.presentation.Route


fun NavGraphBuilder.homeScreenRoute(navigate: (Route) -> Unit) {
    composable<HomeScreenRoute> {
        HomeScreen(navigate)
    }
}

@Composable
fun HomeScreen(navigate: (Route) -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(
            onClick = { navigate(ChatScreenRoute) },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Start Chat Admin")
        }

    }

}