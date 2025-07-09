package com.ht.websocket.presentation

import kotlinx.serialization.Serializable



interface Route

@Serializable
data object PopBackStackRoute: Route
@Serializable
data object ChatScreenRoute: Route
@Serializable
data object HomeScreenRoute: Route