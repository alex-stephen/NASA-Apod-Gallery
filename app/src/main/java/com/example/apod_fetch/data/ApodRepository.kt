package com.example.apod_fetch.data

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

class ApodRepository(private val apodDao: ApodDao) {

    //Get All Apod Photos
    suspend fun getAllApods(): List<ApodPhoto> {
        return apodDao.getAllApods()
    }

    // Get A Random Set of APODs from the database
    fun getRandomPhotos(number: Int): Flow<List<ApodPhoto>> {
        val query = buildRandomPhotosQuery(number)
        return apodDao.getRandomPhotos(query)
    }

    // Get All Favourite APODs from the database
    fun getFavouritePhotos(): Flow<List<ApodPhoto>> {
        return apodDao.getFavouritePhotos()
    }

    // Get all APODs from the database
    fun getApodsByRange(startDate: String, endDate: String): Flow<List<ApodPhoto>> {
        return apodDao.getApodByRange(startDate, endDate)
    }

    // Get a single APOD by date
    fun getApodByDate(date: String): Flow<ApodPhoto?> {
        return apodDao.getApodByDate(date)
    }

    // Insert a list of APODs into the database
    suspend fun insertApods(listOfApods: List<ApodPhoto>) {
        apodDao.insertApods(listOfApods)
    }

    // Insert an APODs into the database
    suspend fun insertPhoto(Apod: ApodPhoto) {
        apodDao.insertPhoto(Apod)
    }

    // Update An APOD Photo
    suspend fun updatePhoto(photo: ApodPhoto) {
        apodDao.update(photo)
    }
}

fun buildRandomPhotosQuery(number: Int): SupportSQLiteQuery {
    val query = "SELECT * FROM apod ORDER BY RANDOM() LIMIT ?"
    return SimpleSQLiteQuery(query, arrayOf(number))
}