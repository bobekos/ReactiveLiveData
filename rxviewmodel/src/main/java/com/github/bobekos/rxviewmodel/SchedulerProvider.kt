package com.github.bobekos.rxviewmodel

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


data class SchedulerProvider(
        val observeScheduler: Scheduler = AndroidSchedulers.mainThread(),
        val subscribeScheduler: Scheduler = Schedulers.io())