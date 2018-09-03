package com.github.bobekos.reactivelivedata

import android.arch.lifecycle.LiveData
import android.support.annotation.NonNull
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicReference


class FlowableReactiveSource<T>(@NonNull private val source: Flowable<T>) : LiveData<Optional<T>>() {

    companion object {
        fun <T> from(@NonNull source: Flowable<T>, subscribeScheduler: Scheduler = Schedulers.io()): LiveData<Optional<T>> {
            return FlowableReactiveSource(source.subscribeOn(subscribeScheduler))
        }
    }

    private val subscriber = AtomicReference<FlowableDataSubscriber>()

    override fun onActive() {
        super.onActive()
        val s = FlowableDataSubscriber()
        subscriber.set(s)
        source.subscribe(s)
    }

    override fun onInactive() {
        super.onInactive()

        subscriber.getAndSet(null)?.dispose()
    }

    inner class FlowableDataSubscriber : AtomicReference<Subscription>(), Subscriber<T> {

        override fun onComplete() {
            postValue(Optional.Complete())

            subscriber.compareAndSet(this, null)
        }

        override fun onSubscribe(s: Subscription) {
            if (compareAndSet(null, s)) {
                s.request(Long.MAX_VALUE)
            } else {
                s.cancel()
            }
        }

        override fun onNext(t: T) {
            postValue(Optional.Result(t))
        }

        override fun onError(t: Throwable) {
            postValue(Optional.Exception(t))

            subscriber.compareAndSet(this, null)
        }

        fun dispose() {
            get()?.cancel()
        }

    }

}