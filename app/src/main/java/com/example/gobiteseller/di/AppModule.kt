package com.example.gobiteseller.di

import com.example.gobiteseller.data.local.PreferencesHelper
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {

    single {
        Gson()
    }

    single {
        PreferencesHelper(get())
    }

}