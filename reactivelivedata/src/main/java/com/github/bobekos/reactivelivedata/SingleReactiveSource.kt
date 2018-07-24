package com.github.bobekos.reactivelivedata

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.NonNull
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicReference


class SingleReactiveSource<T>(@NonNull private val source: Single<T>) : LiveData<Optional<T>>() {

    companion object {
        fun <T> from(@NonNull source: Single<T>, subscribeScheduler: Scheduler = Schedulers.io()): LiveData<Optional<T>> {
            return SingleReactiveSource(source.subscribeOn(subscribeScheduler))
        }
    }

    private val subscriber = AtomicReference<SingleDataSubscriber>()
    private val observerReference = AtomicReference<Observer<Optional<T>>>()

    override fun onActive() {
        super.onActive()

        val s = SingleDataSubscriber()
        subscriber.set(s)
        source.subscribe(s)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<Optional<T>>) {
        super.observe(owner, observer)

        observerReference.compareAndSet(null, observer)
    }

    override fun onInactive() {
        super.onInactive()

        subscriber.getAndSet(null)?.dispose()
        removeObserver(observerReference.getAndSet(null))
    }


    inner class SingleDataSubscriber : AtomicReference<Disposable>(), SingleObserver<T> {

        override fun onSuccess(t: T) {
            postValue(Optional.Result(t))

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