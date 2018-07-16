package com.github.bobekos.rxviewmodelexample.viewmodel

import android.arch.lifecycle.LiveData
import com.github.bobekos.rxviewmodel.Optional
import com.github.bobekos.rxviewmodel.RxViewModel
import com.github.bobekos.rxviewmodel.SchedulerProvider
import com.github.bobekos.rxviewmodel.SingleEvent
import com.github.bobekos.rxviewmodelexample.database.UserDao
import com.github.bobekos.rxviewmodelexample.database.UserEntity
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UserViewModel(val dao: UserDao, private val provider: SchedulerProvider) : RxViewModel() {

    fun insert(id: Int, name: String): CompletableAction {
        return CompletableAction(provider) { dao.insert(UserEntity(id, name)) }
    }

    fun update(id: Int, name: String): CompletableAction {
        return CompletableAction(provider) { dao.updateUser(UserEntity(id, name)) }
    }

    fun getFromSingle(id: Int): ActionFromSingle<UserEntity> {
        return ActionFromSingle(dao.getByIdAsSingle(id), provider)
    }

    fun getFromMaybe(id: Int): ActionFromMaybe<UserEntity> {
        return ActionFromMaybe(dao.getByIdAsMaybe(id), provider)
    }

    fun delete(id: Int, name: String): CompletableAction {
        return CompletableAction(provider) { dao.delete(UserEntity(id, name)) }
    }

    fun loadUser(): LiveData<UserEntity> {
        return liveDataFromFlowable(dao.getUsers())
    }

    fun addDisposableTest2(action: UserViewModel.() -> Disposable) {
        //addDisposable(action())
    }

    fun testSingleToLiveData(): LiveData<Optional<UserEntity>> {
        return SingleEvent.fromSource(dao.getByIdAsSingle(1).subscribeOn(Schedulers.io()))
    }

    fun getTest(id: Int): Single<UserEntity> {
        return dao.getByIdAsSingle(id)
    }

    fun add() {

    }

    fun <T> addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

}