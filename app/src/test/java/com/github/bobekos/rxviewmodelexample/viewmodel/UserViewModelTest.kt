package com.github.bobekos.rxviewmodelexample.viewmodel

import android.arch.persistence.room.EmptyResultSetException
import android.database.sqlite.SQLiteConstraintException
import com.github.bobekos.rxviewmodelexample.database.UserDao
import com.github.bobekos.rxviewmodelexample.database.UserEntity
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@RunWith(JUnit4::class)
class UserViewModelTest {

    private val userDao = mock(UserDao::class.java)
    private val viewModel = UserViewModel(userDao, SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline()))

    @Test
    fun testInsertCompletableError() {
        `when`(userDao.insert(UserEntity(1, "Bobekos"))).thenThrow(SQLiteConstraintException())

        var t: Throwable? = null
        viewModel.insert(1, "Bobekos")
                .run(onError = {
                    t = it
                }, onComplete = {})

        assertThat(t, IsInstanceOf(SQLiteConstraintException::class.java))
    }

    @Test
    fun testInsertCompletableSuccess() {
        var success = false

        viewModel.insert(1, "Bobekos")
                .run(onComplete = {
                    success = true
                })

        assertThat(success, IsEqual(true))
    }

    @Test
    fun testGetFromSingleError() {
        `when`(userDao.getByIdAsSingle(1)).thenReturn(Single.error(EmptyResultSetException("")))

        var t: Throwable? = null
        viewModel.getFromSingle(1)
                .get(onError = {
                    t = it
                }, onSuccess = {})

        assertThat(t, IsInstanceOf(EmptyResultSetException::class.java))
    }

    @Test
    fun testGetFromSingleSuccess() {
        `when`(userDao.getByIdAsSingle(1)).thenReturn(Single.just(UserEntity(1, "Bobekos")))

        var result: UserEntity? = null
        viewModel.getFromSingle(1)
                .get(onSuccess = {
                    result = it
                })

        assertThat(result?.username, IsEqual("Bobekos"))
    }

    @Test
    fun testGetFromMaybeError() {
        `when`(userDao.getByIdAsMaybe(1)).thenReturn(Maybe.error(EmptyResultSetException("")))

        var t: Throwable? = null
        viewModel.getFromMaybe(1)
                .get(onError = {
                    t = it
                }, onComplete = {}, onSuccess = {})

        assertThat(t, IsInstanceOf(EmptyResultSetException::class.java))
    }

    @Test
    fun testGetFromMaybeEmpty() {
        `when`(userDao.getByIdAsMaybe(1)).thenReturn(Maybe.empty())

        var result = false
        viewModel.getFromMaybe(1)
                .get(onComplete = {
                    result = true
                }, onError = {}, onSuccess = {})

        assertThat(result, IsEqual(true))
    }

    @Test
    fun testGetFromMaybeSuccess() {
        `when`(userDao.getByIdAsMaybe(1)).thenReturn(Maybe.just(UserEntity(1, "Bobekos")))

        var result: UserEntity? = null
        viewModel.getFromMaybe(1)
                .get(onSuccess = {
                    result = it
                })

        assertThat(result?.username, IsEqual("Bobekos"))
    }

    @Test
    fun testDeleteUser() {
        var success = false

        viewModel.insert(1, "Bobekos")
                .run(onComplete = {
                    success = true
                })

        assertThat(success, IsEqual(true))
    }
}