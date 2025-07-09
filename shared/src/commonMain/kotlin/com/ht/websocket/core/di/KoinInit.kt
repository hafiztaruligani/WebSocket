package com.ht.websocket.core.di

import com.ht.websocket.core.network.networkModule
import com.ht.websocket.data.di.dataModule
import com.ht.websocket.presentation.di.viewModelModule
import org.koin.core.module.Module



private val modules: MutableList<Module> = mutableListOf( // default modules
    networkModule,
    viewModelModule,
    dataModule
)


interface KoinInitInterface {

    fun addExtraModule(module: Module) {
        modules.add(module)
    }

    fun init() {
        startKoin(
            modules
        )
    }

    fun startKoin(appModules: List<Module>)
}


expect class KoinInit: KoinInitInterface {
    override fun startKoin(appModules: List<Module>)
}
