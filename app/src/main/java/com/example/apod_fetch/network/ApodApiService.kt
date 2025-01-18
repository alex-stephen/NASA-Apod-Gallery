package com.example.apod_fetch.network

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.apod_fetch.data.ApodPhoto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL =
    "https://api.nasa.gov/planetary/"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(120, TimeUnit.SECONDS) // Set connection timeout
    .readTimeout(120, TimeUnit.SECONDS)    // Set read timeout
    .writeTimeout(120, TimeUnit.SECONDS)   // Set write timeout
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface ApodApiService {

    // Fetch photos within a date range
    @GET("apod")
    suspend fun getPhotosByDate(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("thumbs") thumbs: Boolean = true
    ): List<ApodPhoto>

    // Fetch Random photos with given a count
    @GET("apod")
    suspend fun getRandomPhotos(
        @Query("api_key") apiKey: String,
        @Query("count") number: Int,
        @Query("thumbs") thumbs: Boolean = true
    ): List<ApodPhoto>

    // Fetch the photo of the day
    @GET("apod")
    suspend fun getPhotoOfTheDay(
        @Query("api_key") apiKey: String
    ): ApodPhoto
}

// Singleton instance of the API service
object ApodApi {
    val retrofitService: ApodApiService by lazy {
        retrofit.create(ApodApiService::class.java)
    }
}

