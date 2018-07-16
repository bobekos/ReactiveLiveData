package com.github.bobekos.reactivelivedata

import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class CompletableReactiveSource constructor(@NonNull private val source: Completable) : LiveData<Optional<Nothing>>() {

    companion object {
        fun from(@NonNull source: Completable, subscribeScheduler: Scheduler = Schedulers.io()): LiveData<Optional<Nothing>> {
            return CompletableReactiveSource(source.subscribeOn(subscribeScheduler))
        }

        fun fromAction(subscribeScheduler: Scheduler = Schedulers.io(), action: () -> Unit): LiveData<Optional<Nothing>> {
            return from(Completable.fromAction { action() }, subscribeScheduler)
        }
    }

    private val subscriber = AtomicReference<CompletableDataSubscriber>()
    private val pendingEvent = AtomicBoolean(false)

    override fun onActive() {
        super.onActive()

        if (pendingEvent.compareAndSet(false, true)) {
            val s = CompletableDataSubscriber()
            subscriber.set(s)
            source.subscribe(s)
        }
    }

    override fun onInactive() {
        super.onInactive()

        subscriber.getAndSet(null)?.dispose()
    }

    inner class CompletableDataSubscriber : AtomicReference<Disposable>(), CompletableObserver {

        override fun onComplete() {
            postValue(Optional.Complete())

            subscriber.compareAndSet(this, null)
        }

        override fun onSubscribe(d: Disposable) {
            compareAndSet(null, d)
        }

        override fun onError(e: Throwable) {
            postValue(Optional.Exception(e))

            subscriber.compareAndSet(this, null)
        }

        fun dispose() {
            get()?.dispose()
        }

    }
}