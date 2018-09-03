package com.github.bobekos.rxviewmodelexample.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteConstraintException
import com.github.bobekos.reactivelivedata.testCompletableSubscribe
import com.github.bobekos.reactivelivedata.testFlowableSubscribe
import com.github.bobekos.reactivelivedata.testMaybeSubscribe
import com.github.bobekos.reactivelivedata.testSingleSubscribe
import com.github.bobekos.rxviewmodelexample.database.UserDao
import com.github.bobekos.rxviewmodelexample.database.UserEntity
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*


@RunWith(JUnit4::class)
class UserViewModelTest {

    private inline fun <reified T> lambdaMock(): T = Mockito.mock(T::class.java)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val userDao = mock(UserDao::class.java)
    private val viewModel = UserViewModel(userDao)

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun testInsertCompletableError() {
        val testObject = SQLiteAbortException()

        `when`(userDao.insert(UserEntity(1, "Bobekos"))).thenThrow(testObject)

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        viewModel.insert(1, "Bobekos").testCompletableSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testInsertCompletableSuccess() {
        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<() -> Unit>()

        viewModel.insert(1, "Bobekos").testCompletableSubscribe(lifecycle, onComplete = observer)

        verify(observer).invoke()
    }

    @Test
    fun testGetFromSingleError() {
        val testObject = SQLiteConstraintException()

        `when`(userDao.getByIdAsSingle(1)).then { Single.error<UserEntity>(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        viewModel.getFromSingle(1).testSingleSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testGetFromSingleSuccess() {
        val testObject = UserEntity(1, "Bobekos")

        `when`(userDao.getByIdAsSingle(1)).then { Single.just(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(t: UserEntity) -> Unit>()

        viewModel.getFromSingle(1).testSingleSubscribe(lifecycle, observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testGetFromMaybeError() {
        val testObject = SQLiteConstraintException()

        `when`(userDao.getByIdAsMaybe(1)).then { Maybe.error<UserEntity>(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        viewModel.getFromMaybe(1).testMaybeSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testGetFromMaybeEmpty() {
        `when`(userDao.getByIdAsMaybe(1)).then { Maybe.empty<UserEntity>() }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<() -> Unit>()

        viewModel.getFromMaybe(1).testMaybeSubscribe(lifecycle, onComplete = observer)

        verify(observer).invoke()
    }

    @Test
    fun testGetFromMaybeSuccess() {
        val testObject = UserEntity(1, "Bobekos")

        `when`(userDao.getByIdAsMaybe(1)).then { Maybe.just(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(t: UserEntity) -> Unit>()

        viewModel.getFromMaybe(1).testMaybeSubscribe(lifecycle, onSuccess = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testLoadUserSuccess() {
        val testObject = UserEntity(1, "Bobekos")

        `when`(userDao.getUsers()).then { Flowable.just(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(t: UserEntity) -> Unit>()

        viewModel.loadUser().testFlowableSubscribe(lifecycle, onNext = observer)

        verify(observer).invoke(testObject)
    }

    @Test
    fun testLoadUserError() {
        val testObject = SQLiteConstraintException()

        `when`(userDao.getUsers()).then { Flowable.error<UserEntity>(testObject) }

        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val observer = lambdaMock<(e: Throwable) -> Unit>()

        viewModel.loadUser().testFlowableSubscribe(lifecycle, onError = observer)

        verify(observer).invoke(testObject)
    }
}