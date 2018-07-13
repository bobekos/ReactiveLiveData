package com.github.bobekos.rxviewmodel

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


data class SchedulerProvider(
        val uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
        val ioScheduler: Scheduler = Schedulers.io())