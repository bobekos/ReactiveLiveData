package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import io.reactivex.MaybeObserver
import io.reactivex.MaybeSource
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class MaybeReactiveSource<T>(@NonNull private val source: MaybeSource<T>) : LiveData<Optional<T>>() {

    companion object {
        fun <T> from(@NonNull source: MaybeSource<T>): LiveData<Optional<T>> {
            return MaybeReactiveSource(source)
        }
    }

    private val subscriber = AtomicReference<MaybeDataSubscriber>()
    private val pendingEvent = AtomicBoolean(false)

    override fun onActive() {
        super.onActive()

        if (pendingEvent.compareAndSet(false, true)) {
            val s = MaybeDataSubscriber()
            subscriber.set(s)
            source.subscribe(s)
        }
    }

    override fun onInactive() {
        super.onInactive()

        subscriber.getAndSet(null)?.dispose()
    }

    inner class MaybeDataSubscriber : AtomicReference<Disposable>(), MaybeObserver<T> {

        override fun onSuccess(t: T) {
            postValue(Optional.Result(t))

            subscriber.compareAndSet(this, null)
        }

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