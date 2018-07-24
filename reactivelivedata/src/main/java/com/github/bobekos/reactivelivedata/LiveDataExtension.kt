package com.github.bobekos.reactivelivedata

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import org.jetbrains.annotations.TestOnly

inline fun <T> LiveData<Optional<T>>.subscribeSingle(owner: LifecycleOwner, crossinline onSuccess: (t: T) -> Unit, crossinline onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}

inline fun <T> LiveData<Optional<T>>.subscribeMaybe(owner: LifecycleOwner, crossinline onSuccess: (t: T) -> Unit, crossinline onError: (e: Throwable) -> Unit = {}, crossinline onComplete: () -> Unit = {}) {
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

inline fun LiveData<Optional<Nothing>>.subscribeCompletable(owner: LifecycleOwner, crossinline onComplete: () -> Unit = {}, crossinline onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Complete -> onComplete()
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}

inline fun <T> LiveData<T>.nonNullObserver(owner: LifecycleOwner, crossinline observer: (t: T) -> Unit, crossinline nullObserver: () -> Unit = {}) {
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
inline fun <T> LiveData<Optional<T>>.testSingleSubscribe(owner: Lifecycle, crossinline onSuccess: (t: T) -> Unit = {}, crossinline onError: (e: Throwable) -> Unit = {}) {
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
inline fun <T> LiveData<Optional<T>>.testMaybeSubscribe(owner: Lifecycle, crossinline onSuccess: (t: T) -> Unit = {}, crossinline onError: (e: Throwable) -> Unit = {}, crossinline onComplete: () -> Unit = {}) {
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
inline fun LiveData<Optional<Nothing>>.testCompletableSubscribe(owner: Lifecycle, crossinline onComplete: () -> Unit = {}, crossinline onError: (e: Throwable) -> Unit = {}) {
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