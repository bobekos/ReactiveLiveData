package com.github.bobekos.reactivelivedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.intellij.lang.annotations.Flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class ReactiveLiveDataTest {

    private inline fun <reified T> lambdaMock(): T = Mockito.mock(T::class.java)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun testSingleSourceOnSuccess() {
        val testObject = "TestString"

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Single.just(testObject)

        val observer = lambdaMock<(t: String) -> Unit>()

        SingleReactiveSource.from(source).testSingleSubscribe(lifecycle, observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testSingleSourceOnError() {
        val testObject = IllegalStateException()

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Single.error<String>(testObject)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        SingleReactiveSource.from(source).testSingleSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testMaybeSourceOnSuccess() {
        val testObject = "TestString"

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Maybe.just(testObject)

        val observer = lambdaMock<(t: String) -> Unit>()

        MaybeReactiveSource.from(source).testMaybeSubscribe(lifecycle, observer)

        verify(observer).invoke("TestString")
    }

    @Test
    fun testMaybeSourceOnError() {
        val testObject = IllegalStateException()

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Maybe.error<String>(testObject)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        MaybeReactiveSource.from(source).testSingleSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testMaybeSourceOnComplete() {
        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Maybe.empty<String>()

        val observer = lambdaMock<() -> Unit>()

        MaybeReactiveSource.from(source).testMaybeSubscribe(lifecycle, onComplete = observer)

        verify(observer).invoke()
    }

    @Test
    fun testCompletableSourceOnComplete() {
        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Completable.fromAction {
            //do stuff
        }

        val observer = lambdaMock<() -> Unit>()

        CompletableReactiveSource.from(source).testCompletableSubscribe(lifecycle, observer)

        verify(observer).invoke()
    }

    @Test
    fun testCompletableSourceOnError() {
        val testObject = IllegalStateException()

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Completable.fromAction {
            throw testObject
        }

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        CompletableReactiveSource.from(source).testCompletableSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testFlowableSourceOnSuccess() {
        val testObject = "TestString"

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Flowable.just(testObject)

        val observer = lambdaMock<(t: String) -> Unit>()

        FlowableReactiveSource.from(source).testFlowableSubscribe(lifecycle, observer)

        verify(observer).invoke("TestString")
    }

    @Test
    fun testFlowableSourceOnError() {
        val testObject = IllegalStateException()

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Flowable.error<String>(testObject)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        FlowableReactiveSource.from(source).testFlowableSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testFlowableSourceOnComplete() {
        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val source = Flowable.empty<String>()

        val observer = lambdaMock<() -> Unit>()

        FlowableReactiveSource.from(source).testFlowableSubscribe(lifecycle, onComplete = observer)

        verify(observer).invoke()
    }
}