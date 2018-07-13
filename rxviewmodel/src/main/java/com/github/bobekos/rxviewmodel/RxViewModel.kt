package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

open class RxViewModel(private val schedulerProvider: SchedulerProvider = SchedulerProvider()) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()

        super.onCleared()
    }

    fun <T> liveDataFromFlowable(flowable: Flowable<T>): LiveData<T> {
        return LiveDataReactiveStreams.fromPublisher(flowable)
    }

    inner class CompletableAction(private val action: () -> Unit) {

        fun run(onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
            disposables.add(Completable.fromAction { action() }
                    .subscribeOn(schedulerProvider.ioScheduler)
                    .observeOn(schedulerProvider.uiScheduler)
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
                    .subscribeOn(schedulerProvider.ioScheduler)
                    .observeOn(schedulerProvider.uiScheduler)
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
                    .subscribeOn(schedulerProvider.ioScheduler)
                    .observeOn(schedulerProvider.uiScheduler)
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

fun <T> LiveData<T>.nonNullObserver(owner: LifecycleOwner, observer: (t: T) -> Unit, nullObserver: () -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            observer(it)
        } else {
            nullObserver()
        }
    })
}