package com.github.bobekos.rxviewmodelexample.database


import android.arch.persistence.room.*
import android.arch.persistence.room.Database
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Database(entities = [(UserEntity::class)], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}

//Dao -> only for this example in same scope
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(model: UserEntity)

    @Query("SELECT * FROM UserEntity WHERE id= :id")
    fun getByIdAsSingle(id: Int): Single<UserEntity>

    @Query("SELECT * FROM UserEntity WHERE id= :id")
    fun getByIdAsMaybe(id: Int): Maybe<UserEntity>

    @Query("SELECT * FROM UserEntity")
    fun getUsers(): Flowable<UserEntity>

    @Update
    fun updateUser(model: UserEntity)

    @Delete
    fun delete(model: UserEntity)

}