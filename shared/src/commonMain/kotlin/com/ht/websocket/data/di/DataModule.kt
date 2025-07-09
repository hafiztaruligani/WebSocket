package com.ht.websocket.data.di

import com.ht.websocket.data.auth.api.repository.AuthRepository
import com.ht.websocket.data.auth.implementation.repository.AuthRepositoryImpl
import com.ht.websocket.data.chat.api.repository.ChatRepository
import com.ht.websocket.data.chat.implementation.repository.ChatRepositoryImpl
import com.ht.websocket.data.socket.SocketManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SocketManager)
    single<AuthRepository> { AuthRepositoryImpl() }
    single<ChatRepository> { ChatRepositoryImpl(get()) }

}