package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import io.reactivex.SingleObserver
import io.reactivex.SingleSource
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class SingleLiveData<T>(@NonNull private val publisher: SingleSource<T>) : LiveData<T>() {

    companion object {
        fun <T> fromSingleSource(@NonNull source: SingleSource<T>) : LiveData<T> {
            return SingleLiveData(source)
        }
    }

    private val pendingEvent = AtomicBoolean(false)

    val subscriber = AtomicReference<SingleDataSubscriber>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, Observer<T> {
            if (pendingEvent.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(value: T) {
        pendingEvent.set(true)
        super.setValue(value)
    }

    override fun onActive() {
        super.onActive()
        val s = SingleDataSubscriber()
        subscriber.set(s)
        publisher.subscribe(s)
    }

    override fun onInactive() {
        super.onInactive()
        subscriber.getAndSet(null)?.cancelSubscription()
    }


    inner class SingleDataSubscriber : AtomicReference<Disposable>(), SingleObserver<T> {

        override fun onSuccess(t: T) {
            postValue(t)
        }

        override fun onSubscribe(d: Disposable) {
            compareAndSet(null, d)
        }

        override fun onError(e: Throwable) {
            subscriber.compareAndSet(this, null)
        }

        fun cancelSubscription() {
            get()?.dispose()
        }
    }

}