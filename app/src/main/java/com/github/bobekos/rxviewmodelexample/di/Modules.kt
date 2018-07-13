package com.github.bobekos.rxviewmodelexample.di

import android.arch.persistence.room.Room
import com.github.bobekos.rxviewmodel.SchedulerProvider
import com.github.bobekos.rxviewmodelexample.database.Database
import com.github.bobekos.rxviewmodelexample.viewmodel.UserViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext


object Modules {

    val database: Module = applicationContext {
        bean {
            Room.databaseBuilder(androidApplication(), Database::class.java, "rxvm_db").build()
        }

        bean {
            get<Database>().getUserDao()
        }
    }

    val vm: Module = applicationContext {
        viewModel {
            UserViewModel(get(), get())
        }
    }

    val schedulerProvider = applicationContext {
        bean { SchedulerProvider() }
    }

}