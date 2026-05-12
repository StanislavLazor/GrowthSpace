package com.lazor.growthspace

import android.app.Application
import com.lazor.growthspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GrowthSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GrowthSpaceApp)
            modules(appModule)
        }
    }
}