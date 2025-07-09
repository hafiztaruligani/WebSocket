package com.ht.websocket

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.ht.websocket.presentation.screen.ChatScreen
import com.ht.websocket.presentation.screen.main.MainScreen


fun MainViewController(
) = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
    }
) {
    CompositionLocalProvider() {
        MainScreen()
    }
}
