package com.example.gobiteseller

import android.app.Application
import com.example.gobiteseller.di.appModule
import com.example.gobiteseller.di.networkModule
import com.example.gobiteseller.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GoBiteSeller: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GoBiteSeller)
            modules(listOf(appModule, networkModule, viewModelModule))
        }
    }
}