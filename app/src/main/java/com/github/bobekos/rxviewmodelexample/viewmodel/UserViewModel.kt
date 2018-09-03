package com.github.bobekos.rxviewmodelexample.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.github.bobekos.reactivelivedata.*
import com.github.bobekos.rxviewmodelexample.database.UserDao
import com.github.bobekos.rxviewmodelexample.database.UserEntity
import io.reactivex.Completable


class UserViewModel(private val dao: UserDao) : ViewModel() {

    fun insert(id: Int, name: String): LiveData<Optional<Nothing>> {
        return CompletableReactiveSource.fromAction {
            dao.insert(UserEntity(id, name))
        }
    }

    fun update(id: Int, name: String): LiveData<Optional<Nothing>> {
        return CompletableReactiveSource.from(Completable.fromAction {
            dao.updateUser(UserEntity(id, name))
        })
    }

    fun getFromSingle(id: Int): LiveData<Optional<UserEntity>> {
        return SingleReactiveSource.from(dao.getByIdAsSingle(id))
    }

    fun getFromMaybe(id: Int): LiveData<Optional<UserEntity>> {
        return MaybeReactiveSource.from(dao.getByIdAsMaybe(id))
    }

    fun delete(id: Int, name: String): LiveData<Optional<Nothing>> {
        return CompletableReactiveSource.from(Completable.fromAction {
            dao.delete(UserEntity(id, name))
        })
    }

    fun loadUser(): LiveData<Optional<UserEntity>> {
        return FlowableReactiveSource.from(dao.getUsers())
    }
}