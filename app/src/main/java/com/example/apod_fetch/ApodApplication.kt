package com.example.apod_fetch

import android.app.Application
import com.example.apod_fetch.data.ApodDatabase
import com.example.apod_fetch.data.ApodRepository


class ApodApplication : Application() {
    private lateinit var database: ApodDatabase

    lateinit var apodRepository: ApodRepository

    override fun onCreate() {
        super.onCreate()
        database = ApodDatabase.getDatabase(this)
        apodRepository = ApodRepository(database.apodDao())
    }
}