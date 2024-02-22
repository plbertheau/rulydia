package com.plbertheau.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plbertheau.data.Constants.DATABASE_VERSION
import com.plbertheau.domain.entity.RemoteKeys
import com.plbertheau.domain.entity.User

@Database(
    entities = [User::class, RemoteKeys::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class UserLocalDB : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
}