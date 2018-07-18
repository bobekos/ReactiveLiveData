package com.github.bobekos.reactivelivedata

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import org.jetbrains.annotations.TestOnly


fun <T> LiveData<Optional<T>>.subscribeSingle(owner: LifecycleOwner, onSuccess: (t: T) -> Unit, onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}

fun <T> LiveData<Optional<T>>.subscribeMaybe(owner: LifecycleOwner, onSuccess: (t: T) -> Unit, onError: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Complete -> onComplete()
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception<T> -> onError(it.throwable)
            }
        }
    })
}

fun LiveData<Optional<Nothing>>.subscribeCompletable(owner: LifecycleOwner, onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Complete -> onComplete()
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
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

//region test env.
@TestOnly
fun <T> LiveData<Optional<T>>.testSingleSubscribe(owner: Lifecycle, onSuccess: (t: T) -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
    this.observe({ owner }, {
        if (it != null) {
            when (it) {
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}

@TestOnly
fun <T> LiveData<Optional<T>>.testMaybeSubscribe(owner: Lifecycle, onSuccess: (t: T) -> Unit = {}, onError: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}) {
    this.observe({ owner }, {
        if (it != null) {
            when (it) {
                is Optional.Complete -> onComplete()
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception<T> -> onError(it.throwable)
            }
        }
    })
}

@TestOnly
fun LiveData<Optional<Nothing>>.testCompletableSubscribe(owner: Lifecycle, onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
    this.observe({ owner }, {
        if (it != null) {
            when (it) {
                is Optional.Complete -> onComplete()
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}
//endregion