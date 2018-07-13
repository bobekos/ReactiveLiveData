package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable

open class RxViewModel() : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()

        super.onCleared()
    }

    fun <T> liveDataFromFlowable(flowable: Flowable<T>): LiveData<T> {
        return LiveDataReactiveStreams.fromPublisher(flowable)
    }

    inner class CompletableAction(
            private val provider: SchedulerProvider = SchedulerProvider(),
            private val action: () -> Unit) {

        fun run(onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
            disposables.add(Completable.fromAction { action() }
                    .withProvider(provider)
                    .subscribe(
                            {
                                onComplete()
                            },
                            {
                                onError(it)
                            }
                    )
            )
        }

    }

    inner class ActionFromSingle<T>(private val single: Single<T>) {

        fun get(onSuccess: (result: T) -> Unit, onError: (e: Throwable) -> Unit = {}) {
            disposables.add(single
                    .subscribe(
                            {
                                onSuccess(it)
                            },
                            {
                                onError(it)
                            }
                    ))
        }

    }

    inner class ActionFromMaybe<T>(private val maybe: Maybe<T>) {

        fun get(onSuccess: (result: T) -> Unit, onError: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}) {
            disposables.add(maybe
                    .subscribe(
                            {
                                onSuccess(it)
                            },
                            {
                                onError(it)
                            },
                            {
                                onComplete()
                            }
                    )
            )
        }

    }
}

fun <T> Single<T>.withProvider(provider: SchedulerProvider = SchedulerProvider()): Single<T> {
    return this.subscribeOn(provider.subscribeScheduler).observeOn(provider.observeScheduler)
}

fun <T> Maybe<T>.withProvider(provider: SchedulerProvider = SchedulerProvider()): Maybe<T> {
    return this.subscribeOn(provider.subscribeScheduler).observeOn(provider.observeScheduler)
}

fun Completable.withProvider(provider: SchedulerProvider = SchedulerProvider()): Completable {
    return this.subscribeOn(provider.subscribeScheduler).observeOn(provider.observeScheduler)
}

fun <T> LiveData<T>.nonNullObserver(owner: LifecycleOwner, observer: (t: T) -> Unit, nullObserver: () -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            observer(it)
        } else {
            nullObserver()
        }
    })
}