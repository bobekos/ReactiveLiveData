package com.github.bobekos.rxviewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer


fun <T> LiveData<Optional<T>>.singleObserver(owner: LifecycleOwner, onSuccess: (t: T) -> Unit, onError: (e: Throwable) -> Unit = {}) {
    this.observe(owner, Observer {
        if (it != null) {
            when (it) {
                is Optional.Result<T> -> onSuccess(it.result)
                is Optional.Exception -> onError(it.throwable)
            }
        }
    })
}

fun <T> LiveData<Optional<T>>.maybeObserver(owner: LifecycleOwner, onSuccess: (t: T) -> Unit, onError: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}) {
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

fun LiveData<Optional<Nothing>>.completableObserver(owner: LifecycleOwner, onComplete: () -> Unit = {}, onError: (e: Throwable) -> Unit = {}) {
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