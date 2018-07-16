package com.github.bobekos.rxviewmodel


sealed class Optional<T> {
    class Complete<T> : Optional<T>()
    data class Result<T>(val result: T) : Optional<T>()
    data class Exception<T>(val throwable: Throwable) : Optional<T>()
}