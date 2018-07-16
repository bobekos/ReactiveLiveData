package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class RxViewModel() : ViewModel() {

    val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()

        super.onCleared()
    }

    val <T> T.foo: (T) -> Unit
        get() = { x -> }

    fun addDisposableTest(action: RxViewModel.() -> Disposable) {
        disposables.add(action())
    }

    fun <T> liveDataFromFlowable(flowable: Flowable<T>): LiveData<T> {
        return LiveDataReactiveStreams.fromPublisher(flowable)
    }

    inner class CompletableAction(
            private val schedulerProvider: SchedulerProvider = SchedulerProvider(),
            private val action: () -> Unit) {

        fun run(onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
            disposables.add(Completable.fromAction { action() }
                    .withProvider(schedulerProvider)
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

    inner class Test<T>(private val single: Single<T>) {

        fun get(result: Single<T>.() -> Unit) {

        }
    }

    inner class ActionFromSingle<T>(private val single: Single<T>,
                                    private val schedulerProvider: SchedulerProvider = SchedulerProvider()) {

        fun get(onSuccess: (result: T) -> Unit, onError: (e: Throwable) -> Unit = {}) {
            disposables.add(single
                    .withProvider(schedulerProvider)
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

    inner class ActionFromMaybe<T>(private val maybe: Maybe<T>,
                                   private val schedulerProvider: SchedulerProvider = SchedulerProvider()) {

        fun get(onSuccess: (result: T) -> Unit, onError: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}) {
            disposables.add(maybe
                    .withProvider(schedulerProvider)
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

fun <T> Single<T>.withProvider(provider: SchedulerProvider): Single<T> {
    return this.subscribeOn(provider.subscribeScheduler).observeOn(provider.observeScheduler)
}

fun <T> Maybe<T>.withProvider(provider: SchedulerProvider): Maybe<T> {
    return this.subscribeOn(provider.subscribeScheduler).observeOn(provider.observeScheduler)
}

fun Completable.withProvider(provider: SchedulerProvider): Completable {
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

fun <T> LiveData<Optional<T>>.optionalObserver(owner: LifecycleOwner, onSuccess: (t: T) -> Unit, onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception<T> -> onError(it.throwable)
            }
        }
    })
}