package com.ht.websocket.presentation.screen.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ht.websocket.data.socket.SocketManager
import com.ht.websocket.presentation.HomeScreenRoute
import com.ht.websocket.presentation.MyApplicationTheme
import com.ht.websocket.presentation.PopBackStackRoute
import com.ht.websocket.presentation.Route
import com.ht.websocket.presentation.screen.chatScreenRoute
import com.ht.websocket.presentation.screen.home.homeScreenRoute
import io.ktor.client.HttpClient
import org.koin.compose.koinInject


@Composable
fun MainScreen() {
    MyApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.displayCutout,
        )
        { innerPadding ->

            val navController: NavHostController = rememberNavController()

            fun navigate(route: Route) {
                if (route is PopBackStackRoute) navController.popBackStack()
                else navController.navigate(route)
            }


            NavHost(
                navController = navController,
                startDestination = HomeScreenRoute
            ) {

                homeScreenRoute(::navigate)
                chatScreenRoute(::navigate)

            }




        }
    }
}