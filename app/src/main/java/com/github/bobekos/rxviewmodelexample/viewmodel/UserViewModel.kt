package com.github.bobekos.rxviewmodelexample.viewmodel

import android.arch.lifecycle.LiveData
import com.github.bobekos.rxviewmodel.RxViewModel
import com.github.bobekos.rxviewmodel.SchedulerProvider
import com.github.bobekos.rxviewmodelexample.database.UserDao
import com.github.bobekos.rxviewmodelexample.database.UserEntity


class UserViewModel(private val dao: UserDao, private val provider: SchedulerProvider) : RxViewModel() {

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

}