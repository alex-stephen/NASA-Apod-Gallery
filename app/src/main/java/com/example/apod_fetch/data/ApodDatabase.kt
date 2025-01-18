package com.example.apod_fetch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ApodPhoto::class], version = 1, exportSchema = false)
abstract class ApodDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    companion object {
        @Volatile
        private var Instance: ApodDatabase? = null

        fun getDatabase(context: Context): ApodDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ApodDatabase::class.java, "apod_database")
                    .build().also { Instance = it }
            }
        }
    }
}