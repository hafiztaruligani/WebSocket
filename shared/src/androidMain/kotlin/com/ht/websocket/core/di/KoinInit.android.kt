package com.ht.websocket.core.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.logger.Level
import org.koin.core.module.Module


actual class KoinInit(
    private val context: Context
) : KoinInitInterface {
    actual override fun startKoin(appModules: List<Module>) {
        org.koin.core.context.startKoin {
            androidContext(context)
            androidLogger(Level.INFO)
            modules(appModules)
        }
    }
}

// be careful to use this context
object AndroidApplicationContext: KoinComponent {
    val applicationContext: Context = get()
}