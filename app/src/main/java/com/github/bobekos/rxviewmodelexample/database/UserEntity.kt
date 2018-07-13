package com.github.bobekos.rxviewmodelexample.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
data class UserEntity(@PrimaryKey val id: Int, val username: String)