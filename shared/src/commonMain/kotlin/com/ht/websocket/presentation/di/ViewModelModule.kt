package com.ht.websocket.presentation.di

import com.ht.websocket.presentation.screen.chat.ChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val viewModelModule = module {
    viewModelOf(::ChatViewModel)
}