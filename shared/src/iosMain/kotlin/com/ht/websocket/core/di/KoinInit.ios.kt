package com.ht.websocket.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

actual class KoinInit : KoinInitInterface {
    actual override fun startKoin(appModules: List<Module>) {
        startKoin {
            modules(appModules)
        }
    }
}

fun InitKoin() = KoinInit().init()