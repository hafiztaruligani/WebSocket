package com.ht.websocket.android

import android.app.Application
import com.ht.websocket.core.di.KoinInit

class App: Application() {

    private val koinInit by lazy { KoinInit(this) }

    override fun onCreate() {
        super.onCreate()
        koinInit.init()
    }

}
