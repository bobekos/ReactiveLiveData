package com.github.bobekos.rxviewmodelexample

import android.app.Application
import com.github.bobekos.rxviewmodelexample.di.Modules
import org.koin.android.ext.android.startKoin


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(
                Modules.vm,
                Modules.database,
                Modules.schedulerProvider
        ))
    }

}